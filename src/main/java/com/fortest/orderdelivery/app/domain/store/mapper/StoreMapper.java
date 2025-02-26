package com.fortest.orderdelivery.app.domain.store.mapper;

import com.fortest.orderdelivery.app.domain.area.entity.Area;
import com.fortest.orderdelivery.app.domain.category.entity.Category;
import com.fortest.orderdelivery.app.domain.category.entity.CategoryStore;
import com.fortest.orderdelivery.app.domain.store.dto.*;
import com.fortest.orderdelivery.app.domain.store.entity.Store;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.CollectionUtils;

public class StoreMapper {

    public static Store storeSaveRequestDtoToEntity(StoreSaveRequestDto storeSaveRequestDto, Area area, String userName) {
        return Store.builder()
                .name(storeSaveRequestDto.getStoreName())
                .area(area)
                .detailAddress(storeSaveRequestDto.getDetailAddress())
                .ownerName(userName)
                .build();
    }

    public static StoreSaveResponseDto entityToStoreSaveResponseDto(Store store, Area area, String userName) {
        return StoreSaveResponseDto.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .areaId(area.getId())
                .detailAddress(store.getDetailAddress())
                .ownerName(userName)
                .build();
    }

    public static StoreGetDetailResponseDto entityToStoreGetDetailResponseDto(Store store) {
        return StoreGetDetailResponseDto.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .areaId(store.getArea().getId())
                .detailAddress(store.getDetailAddress())
                .ownerName(store.getOwnerName())
                .build();
    }

    public static StoreUpdateResponseDto entityToStoreUpdateResponseDto(Store store, Area area) {
        return StoreUpdateResponseDto.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .areaId(area.getId())
                .detailAddress(store.getDetailAddress())
                .ownerName(store.getOwnerName())
                .build();
    }

    public static StoreDeleteResponseDto entityToCategoryDeleteResponseDto(Store store) {
        return StoreDeleteResponseDto.builder()
                .storeId(store.getId())
                .build();
    }

    public static MenuOptionValidRequestDto storeValidRequestDtoToMenuValidRedDto(String storeId, StoreMenuValidRequestDto storeMenuValidRequestDto) {
        List<MenuOptionValidRequestDto.MenuDto> afterMenuDtoList = new ArrayList<>();
        List<StoreMenuValidRequestDto.MenuDto> beforeMenuDtoList = storeMenuValidRequestDto.getMenuList();
        for (StoreMenuValidRequestDto.MenuDto beforeMenuDto : beforeMenuDtoList) {
            List<MenuOptionValidRequestDto.OptionDto> afterOptionList = new ArrayList<>();
            MenuOptionValidRequestDto.MenuDto afterMenuDto = MenuOptionValidRequestDto.MenuDto.builder()
                    .id(beforeMenuDto.getId())
                    .optionList(afterOptionList)
                    .build();
            afterMenuDtoList.add(afterMenuDto);
            List<StoreMenuValidRequestDto.OptionDto> beforeOptionDtoList = beforeMenuDto.getOptionList();
            for (StoreMenuValidRequestDto.OptionDto beforeOptionDto : beforeOptionDtoList) {
                MenuOptionValidRequestDto.OptionDto afterOption = MenuOptionValidRequestDto.OptionDto.builder()
                        .id(beforeOptionDto.getId())
                        .build();
                afterOptionList.add(afterOption);
            }
        }
        return MenuOptionValidRequestDto.builder()
            .menuList(afterMenuDtoList)
            .storeId(storeId)
            .build();
    }

    public static StoreMenuValidResponseDto menuOptionResponseDtoToStoreValidResDto(Store store, MenuOptionValidReponseDto menuOptionValidReponseDto) {
        List<StoreMenuValidResponseDto.MenuDto> afterMenuDtoList = new ArrayList<>();
        List<MenuOptionValidReponseDto.MenuDto> beforeMenuDtoList = menuOptionValidReponseDto.getMenuList();

        if(CollectionUtils.isEmpty(beforeMenuDtoList)) {
            return StoreMenuValidResponseDto.builder()
                .result(menuOptionValidReponseDto.getResult())
                .storeId(store.getId())
                .storeName(store.getName())
                .menuList(new ArrayList<>())
                .build();
        }
        for (MenuOptionValidReponseDto.MenuDto beforeMenuDto : beforeMenuDtoList) {
            List<StoreMenuValidResponseDto.OptionDto> afterOptionList = new ArrayList<>();
            StoreMenuValidResponseDto.MenuDto afterMenuDto = StoreMenuValidResponseDto.MenuDto.builder()
                    .id(beforeMenuDto.getId())
                    .price(beforeMenuDto.getPrice())
                    .name(beforeMenuDto.getName())
                    .optionList(afterOptionList)
                    .build();
            afterMenuDtoList.add(afterMenuDto);
            List<MenuOptionValidReponseDto.OptionDto> beforeOptionDtoList = beforeMenuDto.getOptionList();
            for (MenuOptionValidReponseDto.OptionDto beforeOptionDto : beforeOptionDtoList) {
                StoreMenuValidResponseDto.OptionDto afterOption = StoreMenuValidResponseDto.OptionDto.builder()
                        .id(beforeOptionDto.getId())
                        .price(beforeMenuDto.getPrice())
                        .name(beforeMenuDto.getName())
                        .build();
                afterOptionList.add(afterOption);
            }
        }
         StoreMenuValidResponseDto storeMenuValidResponseDto = StoreMenuValidResponseDto.builder()
                .result(menuOptionValidReponseDto.getResult())
                .storeId(store.getId())
                .storeName(store.getName())
                .menuList(afterMenuDtoList)
                .build();

        return storeMenuValidResponseDto;
    }

    public static StoreSearchResponseDto pageToSearchResponseDto (Page<Store> page, String search) {
        StoreSearchResponseDto.StoreSearchResponseDtoBuilder builder = StoreSearchResponseDto.builder();
        builder = builder
                .search(search == null ? "" : search)
                .totalContents(page.getTotalElements())
                .size(page.getSize())
                .currentPage(page.getNumber() + 1);
        List<StoreSearchResponseDto.storeDto> storeDtoList = page.getContent().stream()
                .map(StoreMapper::entityToPageStoreDto)
                .collect(Collectors.toList());

        return builder
                .storeList(storeDtoList)
                .build();
    }

    public static StoreSearchResponseDto.storeDto entityToPageStoreDto(Store store) {
        return StoreSearchResponseDto.storeDto.builder()
                .storeId(store.getName())
                .storeName(store.getName())
                .area(store.getArea().getPlainAreaName())
                .detailAddress(store.getDetailAddress())
                .ownerName(store.getOwnerName())
                .build();
    }

    public static StoreUpdateCategoryResponseDto entityToUpdateCategoryResponseDto(Store store) {
        List<CategoryStore> categoryStoreList = store.getCategoryStoreList();
        List<StoreUpdateCategoryResponseDto.CategoryDto> categoryDtoList = new ArrayList<>();
        for (CategoryStore categoryStore : categoryStoreList) {
            Category category = categoryStore.getCategory();
            categoryDtoList.add(
                    StoreUpdateCategoryResponseDto.CategoryDto.builder()
                            .categoryId(category.getId())
                            .name(category.getName())
                            .build()
            );
        }
        return StoreUpdateCategoryResponseDto.builder().categoryList(categoryDtoList).build();
    }
}
