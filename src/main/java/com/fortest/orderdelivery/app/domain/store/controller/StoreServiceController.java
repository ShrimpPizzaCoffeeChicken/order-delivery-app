package com.fortest.orderdelivery.app.domain.store.controller;

import com.fortest.orderdelivery.app.domain.store.dto.StoreSaveRequestDto;
import com.fortest.orderdelivery.app.domain.store.dto.StoreSaveResponseDto;
import com.fortest.orderdelivery.app.domain.store.dto.StoreUpdateRequestDto;
import com.fortest.orderdelivery.app.domain.store.dto.StoreUpdateResponseDto;
import com.fortest.orderdelivery.app.domain.store.service.StoreService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/service")
@RequiredArgsConstructor
public class StoreServiceController {

    private final StoreService storeService;

    @PostMapping("/stores")
    public ResponseEntity<CommonDto<StoreSaveResponseDto>> saveStore(@RequestBody StoreSaveRequestDto storeSaveRequestDto) {
        // TODO : TEMP : userId 를 UserDetail 에서 획득해야함
        StoreSaveResponseDto storeSaveResponseDto = storeService.saveStore(storeSaveRequestDto, 123L);

        return ResponseEntity.ok(
                CommonDto.<StoreSaveResponseDto>builder()
                        .message("SUCCESS")
                        .code(HttpStatus.OK.value())
                        .data(storeSaveResponseDto)
                        .build()
        );
    }

    @PatchMapping("/stores/{storeId}")
    public ResponseEntity<CommonDto<StoreUpdateResponseDto>> updateCategory(@PathVariable String storeId, @RequestBody StoreUpdateRequestDto storeUpdateRequestDto){
        StoreUpdateResponseDto storeUpdateResponseDto = storeService.updateStore(storeId, storeUpdateRequestDto);

        return ResponseEntity.ok(
                CommonDto.<StoreUpdateResponseDto>builder()
                        .message("SUCCESS")
                        .code(HttpStatus.OK.value())
                        .data(storeUpdateResponseDto)
                        .build()
        );
    }
}
