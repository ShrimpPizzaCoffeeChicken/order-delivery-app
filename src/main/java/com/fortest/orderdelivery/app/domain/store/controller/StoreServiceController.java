package com.fortest.orderdelivery.app.domain.store.controller;

import com.fortest.orderdelivery.app.domain.store.dto.StoreSaveRequestDto;
import com.fortest.orderdelivery.app.domain.store.dto.StoreSaveResponseDto;
import com.fortest.orderdelivery.app.domain.store.entity.Store;
import com.fortest.orderdelivery.app.domain.store.service.StoreService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/service")
@RequiredArgsConstructor
public class StoreServiceController {

    private final StoreService storeService;

    @PostMapping("/stores")
    public ResponseEntity<CommonDto<StoreSaveResponseDto>> saveStore(@RequestBody StoreSaveRequestDto storeSaveRequestDto) {
        // TODO : TEMP : userId 를 UserDetail 에서 획득해야함
        Store store = storeService.saveStores(storeSaveRequestDto, 123L);
        StoreSaveResponseDto storeSaveResponseDto = StoreSaveResponseDto.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .areaId(store.getArea().getId())
                .detailAddress(store.getDetailAddress())
                .ownerName(store.getOwnerName())
                .build();

        return ResponseEntity.ok(
                CommonDto.<StoreSaveResponseDto>builder()
                        .message("SUCCESS")
                        .code(HttpStatus.OK.value())
                        .data(storeSaveResponseDto)
                        .build()
        );
    }
}
