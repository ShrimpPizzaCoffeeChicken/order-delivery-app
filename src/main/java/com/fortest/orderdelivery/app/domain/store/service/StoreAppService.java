package com.fortest.orderdelivery.app.domain.store.service;

import com.fortest.orderdelivery.app.domain.store.dto.StoreMappingResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreAppService {
    public StoreMappingResponseDto getStoreId(String storeId) {
        return StoreMappingResponseDto.builder()
                .storeId(storeId)
                .build();
    }
}