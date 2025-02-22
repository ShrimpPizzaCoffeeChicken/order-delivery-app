package com.fortest.orderdelivery.app.domain.store.service;

import com.fortest.orderdelivery.app.domain.area.entity.Area;
import com.fortest.orderdelivery.app.domain.area.repository.AreaRepository;
import com.fortest.orderdelivery.app.domain.category.entity.Category;
import com.fortest.orderdelivery.app.domain.category.entity.CategoryStore;
import com.fortest.orderdelivery.app.domain.category.repository.CategoryRepository;
import com.fortest.orderdelivery.app.domain.store.dto.*;
import com.fortest.orderdelivery.app.domain.store.entity.Store;
import com.fortest.orderdelivery.app.domain.store.mapper.StoreMapper;
import com.fortest.orderdelivery.app.domain.store.repository.StoreQueryRepository;
import com.fortest.orderdelivery.app.domain.store.repository.StoreRepository;
import com.fortest.orderdelivery.app.domain.user.entity.RoleType;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.exception.NotFoundException;
import com.fortest.orderdelivery.app.global.exception.NotValidRequestException;
import com.fortest.orderdelivery.app.global.util.JpaUtil;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final MessageUtil messageUtil;
    private final StoreRepository storeRepository;
    private final StoreQueryRepository storeQueryRepository;
    private final AreaRepository areaRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public StoreUpdateCategoryResponseDto updateCategory (String storeId, User user, StoreUpdateCategoryRequestDto requestDto) {

        Store store = storeQueryRepository.findForCategory(storeId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.store")));

        if (user.getRoleType().getRoleName() == RoleType.RoleName.OWNER) {
            if (!store.getOwnerName().equals(user.getUsername())) {
                throw new NotValidRequestException(messageUtil.getMessage("app.order.not-valid-user"));
            }
        }

        List<String> deleteCategoryIdList = requestDto.getDeleteCategoryIdList();
        List<CategoryStore> deleteTargetList = store.getCategoryStoreList().stream()
                .filter(categoryStore -> deleteCategoryIdList.contains(categoryStore.getCategory().getId()))
                .collect(Collectors.toList());
        store.getCategoryStoreList().removeAll(deleteTargetList);

        List<String> addCategoryIdList = requestDto.getAddCategoryIdList();
        List<Category> newCategoryList = categoryRepository.findAllById(addCategoryIdList);
        for (Category newCategory : newCategoryList) {
            CategoryStore categoryStore = new CategoryStore();
            categoryStore.bindCategory(newCategory);
            newCategory.isUpdatedNow(user.getId());
            categoryStore.bindStore(store);
        }

        return StoreMapper.entityToUpdateCategoryResponseDto(store);
    }

    @Transactional
    public StoreSearchResponseDto searchStore (Integer page, Integer size, String orderby, String sort, String search,
                                               String categoryId,
                                               String city, String district, String street) {

        // 지역의 일부 조건이 일치하는 모든 store 를 반환
        PageRequest pageable = JpaUtil.getNormalPageable(page, size, orderby, sort);
        Page<Store> pageStore = storeQueryRepository.findStoreList(pageable, categoryId, city, district, street, search);

        return StoreMapper.pageToSearchResponseDto(pageStore, search);
    }

    @Transactional
    public StoreSaveResponseDto saveStore(StoreSaveRequestDto storeSaveRequestDto, User user) {

        String areaId = storeSaveRequestDto.getAreaId();

        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new BusinessLogicException(messageUtil.getMessage("api.call.client-error")));

        Store newStore = StoreMapper.storeSaveRequestDtoToEntity(storeSaveRequestDto, area);
        newStore.isCreatedBy(user.getId());
        Store savedStore = storeRepository.save(newStore);

        return StoreMapper.entityToStoreSaveResponseDto(savedStore, area);
    }

    public StoreGetDetailResponseDto getStoreDetail(String storeId){

        Store store = storeQueryRepository.findStoreDetail(storeId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.store")));

        return StoreMapper.entityToStoreGetDetailResponseDto(store);
    }

    @Transactional
    public StoreUpdateResponseDto updateStore(String storeId, StoreUpdateRequestDto storeUpdateRequestDto, User user){

        String storeName = storeUpdateRequestDto.getStoreName();
        String areaId = storeUpdateRequestDto.getAreaId();
        String detailAddress = storeUpdateRequestDto.getDetailAddress();
        String ownerName = storeUpdateRequestDto.getOwnerName();

        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new BusinessLogicException(messageUtil.getMessage("api.call.client-error")));

        Store store = storeRepository.findById(storeId).orElseThrow(()->
                new BusinessLogicException(messageUtil.getMessage( "api.call.client-error")));

        store.update(storeName, area, detailAddress, ownerName);

        store.isUpdatedNow(user.getId());

        return StoreMapper.entityToStoreUpdateResponseDto(store, area);
    }

    @Transactional
    public StoreDeleteResponseDto deleteStore(String storeId, Long userId){

        Store store = storeRepository.findById(storeId).orElseThrow(()->
                new BusinessLogicException(messageUtil.getMessage("api.call.client-error")));

        store.isDeletedNow(userId);

        return StoreMapper.entityToCategoryDeleteResponseDto(store);
    }
}
