package com.fortest.orderdelivery.app.domain.store.service;

import com.fortest.orderdelivery.app.domain.ai.dto.StoreResponseDto;
import com.fortest.orderdelivery.app.domain.store.dto.*;
import com.fortest.orderdelivery.app.domain.store.entity.Store;
import com.fortest.orderdelivery.app.domain.store.mapper.StoreMapper;
import com.fortest.orderdelivery.app.domain.store.repository.StoreRepository;
import com.fortest.orderdelivery.app.global.exception.NotFoundException;
import com.fortest.orderdelivery.app.global.gateway.ApiGateway;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreAppService {

    private final ApiGateway apiGateway;
    private final MessageUtil messageUtil;
    private final StoreRepository storeRepository;

    public StoreResponseDto getStoreCheck(String storeId) {
        // store
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.store")));

        return StoreResponseDto.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .area(store.getArea().getPlainAreaName())
                .detailAddress(store.getDetailAddress())
                .ownerName(store.getOwnerName())
                .build();
    }

    public StoreMenuValidResponseDto getStoreMenuValid(String storeId, StoreMenuValidRequestDto requestDto) {
        // store
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.store")));

        // menu 검증 요청
        MenuOptionValidRequestDto menuOptionValidRequestDto = StoreMapper.storeValidRequestDtoToMenuValidRedDto(storeId, requestDto); // requestDto에 storeId 넣어줘야함
        MenuOptionValidReponseDto menuOptionValidFromApp = apiGateway.getValidMenuOptionFromMenuApp(menuOptionValidRequestDto);

        return StoreMapper.menuOptionResponseDtoToStoreValidResDto(store, menuOptionValidFromApp);
    }
}