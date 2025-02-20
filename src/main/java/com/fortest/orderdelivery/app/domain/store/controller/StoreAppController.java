package com.fortest.orderdelivery.app.domain.store.controller;

import com.fortest.orderdelivery.app.domain.store.dto.StoreMappingResponseDto;
import com.fortest.orderdelivery.app.domain.store.dto.StoreMenuValidRequestDto;
import com.fortest.orderdelivery.app.domain.store.dto.StoreMenuValidResponseDto;
import com.fortest.orderdelivery.app.domain.store.service.StoreAppService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{storeId}/menus/valid")
    public ResponseEntity<CommonDto<StoreMenuValidResponseDto>> getStoreMenuValid(@PathVariable("storeId") String storeId,
                                                                                  @RequestParam("data") StoreMenuValidRequestDto requestDto) {
        StoreMenuValidResponseDto responseDto = storeAppService.getStoreMenuValid(storeId, requestDto);
        return ResponseEntity.ok(CommonDto.<StoreMenuValidResponseDto>builder()
                .message("store-id get")
                .code(HttpStatus.OK.value())
                .data(responseDto)
                .build());
    }
}
