package com.fortest.orderdelivery.app.domain.store.controller;

import com.fortest.orderdelivery.app.domain.store.dto.StoreCheckResponseDto;
import com.fortest.orderdelivery.app.domain.store.dto.StoreMenuValidRequestDto;
import com.fortest.orderdelivery.app.domain.store.dto.StoreMenuValidResponseDto;
import com.fortest.orderdelivery.app.domain.store.service.StoreAppService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Validated
@RequestMapping("/api/app/stores")
@RequiredArgsConstructor
public class StoreAppController {

    private final MessageUtil messageUtil;
    private final StoreAppService storeAppService;

    @GetMapping("/{storeId}")
    public ResponseEntity<CommonDto<StoreCheckResponseDto>> getStoreCheck(@PathVariable("storeId") String storeId) {

        StoreCheckResponseDto storeCheckResponseDto = storeAppService.getStoreCheck(storeId);

        return ResponseEntity.ok(
                CommonDto.<StoreCheckResponseDto>builder()
                .message(messageUtil.getSuccessMessage())
                .code(HttpStatus.OK.value())
                .data(storeCheckResponseDto)
                .build());
    }

    @PostMapping("/{storeId}/menus/valid")
    public ResponseEntity<CommonDto<StoreMenuValidResponseDto>> getStoreMenuValid(@PathVariable("storeId") String storeId,
                                                                                  @RequestBody StoreMenuValidRequestDto requestDto) {
        StoreMenuValidResponseDto responseDto = storeAppService.getStoreMenuValid(storeId, requestDto);
        return ResponseEntity.ok(CommonDto.<StoreMenuValidResponseDto>builder()
                .message(messageUtil.getSuccessMessage())
                .code(HttpStatus.OK.value())
                .data(responseDto)
                .build());
    }
}
