package com.fortest.orderdelivery.app.domain.store.service;

import com.fortest.orderdelivery.app.domain.area.entity.Area;
import com.fortest.orderdelivery.app.domain.area.repository.AreaRepository;
import com.fortest.orderdelivery.app.domain.store.dto.*;
import com.fortest.orderdelivery.app.domain.store.entity.Store;
import com.fortest.orderdelivery.app.domain.store.mapper.StoreMapper;
import com.fortest.orderdelivery.app.domain.store.repository.StoreQueryRepository;
import com.fortest.orderdelivery.app.domain.store.repository.StoreRepository;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.exception.NotFoundException;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final AreaRepository areaRepository;
    private final StoreQueryRepository storeQueryRepository;
    private final MessageSource messageSource;
    private final MessageUtil messageUtil;

    @Transactional
    public StoreSaveResponseDto saveStore(StoreSaveRequestDto storeSaveRequestDto, Long userId) {

        String areaId = storeSaveRequestDto.getAreaId();

        // TODO : 유저 검색
        CommonDto<UserResponseDto> validUserResponse = getUserId(userId); // api 요청
        if (validUserResponse == null || validUserResponse.getData() == null) {
            throw new BusinessLogicException(messageSource.getMessage("api.call.client-error", null, Locale.KOREA));
        }
        throwByRespCode(validUserResponse.getCode());
        String username = validUserResponse.getData().getUsername();

        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new BusinessLogicException(messageSource.getMessage("api.call.client-error", null, Locale.KOREA)));

        Store newStore = StoreMapper.toStore(storeSaveRequestDto, area);
        Store savedStore = storeRepository.save(newStore);

        return StoreMapper.toStoreSaveResponseDto(savedStore, area);
    }
    
    // TODO : 유저 조회 : 하단 코드로 교체 예정
    public CommonDto<UserResponseDto> getUserId(Long id) {
        UserResponseDto userDto = UserResponseDto.builder()
                .id(id)
                .build();

        return new CommonDto<>("SUCCESS", HttpStatus.OK.value(), userDto);
    }

//    public CommonDto<UserResponseDto> getUserId(Long id) {
//        String targetUrl = USER_APP_URL
//                .replace("{host}", "localhost")
//                .replace("{port}", "8082")
//                .replace("{userId}", id);
//
//        return webClient.get()
//                .uri(targetUrl)
//                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .retrieve()
//                .bodyToMono(new ParameterizedTypeReference<CommonDto<UserResponseDto>>() {})
//                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) // 에러 발생 시 2초 간격으로 최대 3회 재시작
//                .onErrorResume(throwable -> {
//                    log.error("Fail : {}", targetUrl, throwable);
//                    return Mono.empty();
//                })
//                .block();
//    }

    @Transactional
    public StoreGetDetailResponseDto getStoreDetail(String storeId){

        Store store = storeQueryRepository.findStoreDetail(storeId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.store")));

        return StoreMapper.toStoreGetDetailResponseDto(store);
    }

    @Transactional
    public StoreUpdateResponseDto updateStore(String storeId, StoreUpdateRequestDto storeUpdateRequestDto){

        String storeName = storeUpdateRequestDto.getStoreName();
        String areaId = storeUpdateRequestDto.getAreaId();
        String detailAddress = storeUpdateRequestDto.getDetailAddress();
        String ownerName = storeUpdateRequestDto.getOwnerName();

        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new BusinessLogicException(messageSource.getMessage("api.call.client-error", null, Locale.KOREA)));

        Store store = storeRepository.findById(storeId).orElseThrow(()->
                new BusinessLogicException(messageSource.getMessage( "api.call.client-error",null, Locale.KOREA)));

        store.update(storeName, area, detailAddress, ownerName);

        return StoreMapper.toStoreUpdateResponseDto(store, area);
    }

    @Transactional
    public StoreDeleteResponseDto deleteStore(String storeId, Long userId){

        Store store = storeRepository.findById(storeId).orElseThrow(()->
                new BusinessLogicException(messageSource.getMessage("api.call.client-error",null, Locale.KOREA)));

        store.isDeletedNow(userId);

        return StoreMapper.toCategoryDeleteResponseDto(store);
    }

    private void throwByRespCode(int httpStatusCode) {
        int firstNum = httpStatusCode / 100;
        switch (firstNum) {
            case 4 -> {
                throw new BusinessLogicException(messageSource.getMessage("api.call.client-error", null, Locale.KOREA));
            }
            case 5 -> {
                throw new BusinessLogicException(messageSource.getMessage("api.call.server-error", null, Locale.KOREA));
            }
        }
    }
}
