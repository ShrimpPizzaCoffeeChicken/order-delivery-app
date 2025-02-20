package com.fortest.orderdelivery.app.domain.store.controller;

import com.fortest.orderdelivery.app.domain.store.dto.StoreMappingResponseDto;
import com.fortest.orderdelivery.app.domain.store.service.StoreAppService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/app/store")
@RequiredArgsConstructor
public class StoreAppController {

    private final StoreAppService storeAppService;

    @GetMapping("/{storeId}")
    public ResponseEntity<CommonDto<StoreMappingResponseDto>> getStoreId(@PathVariable String storeId) {
        StoreMappingResponseDto storeMappingResponseDto = storeAppService.getStoreId(storeId);

        return ResponseEntity.ok(CommonDto.<StoreMappingResponseDto>builder()
                .message("store-id get")
                .code(HttpStatus.OK.value())
                .data(storeMappingResponseDto)
                .build());
    }
}
