package com.fortest.orderdelivery.app.domain.store.service;

import com.fortest.orderdelivery.app.domain.store.dto.*;
import com.fortest.orderdelivery.app.domain.store.entity.Store;
import com.fortest.orderdelivery.app.domain.store.mapper.StoreMapper;
import com.fortest.orderdelivery.app.domain.store.repository.StoreRepository;
import com.fortest.orderdelivery.app.global.exception.NotFoundException;
import com.fortest.orderdelivery.app.global.gateway.ApiGateway;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreAppService {

    private final ApiGateway apiGateway;
    private final MessageUtil messageUtil;
    private final StoreRepository storeRepository;

    public StoreCheckResponseDto getStoreCheck(String storeId) {
        // store
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.store")));

        return StoreCheckResponseDto.builder()
                .storeId(store.getId())
                .build();
    }

    public StoreMenuValidResponseDto getStoreMenuValid(String storeId, StoreMenuValidRequestDto requestDto) {
        // store
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.store")));

        // menu 검증 요청
        MenuOptionValidRequestDto menuOptionValidRequestDto = StoreMapper.storeValidRequestDtoToMenuValidRedDto(requestDto);
        MenuOptionValidReponseDto menuOptionValidFromApp = apiGateway.getValidMenuOptionFromMenuApp(store.getId(), menuOptionValidRequestDto);

        return StoreMapper.menuOptionResponseDtoToStoreValidResDto(store, menuOptionValidFromApp);
    }
}