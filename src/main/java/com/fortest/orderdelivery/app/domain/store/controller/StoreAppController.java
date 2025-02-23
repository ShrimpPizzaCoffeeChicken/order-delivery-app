package com.fortest.orderdelivery.app.domain.store.controller;

import com.fortest.orderdelivery.app.domain.store.dto.StoreCheckResponseDto;
import com.fortest.orderdelivery.app.domain.store.dto.StoreMenuValidRequestDto;
import com.fortest.orderdelivery.app.domain.store.dto.StoreMenuValidResponseDto;
import com.fortest.orderdelivery.app.domain.store.service.StoreAppService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/{storeId}")
    public ResponseEntity<CommonDto<StoreCheckResponseDto>> getStoreCheck(
            @Valid @Size(min = 1, max = 50) @PathVariable("storeId") String storeId) {

        log.info(storeId);

        StoreCheckResponseDto storeCheckResponseDto = storeAppService.getStoreCheck(storeId);

        return ResponseEntity.ok(
                CommonDto.<StoreCheckResponseDto>builder()
                .message(messageUtil.getSuccessMessage())
                .code(HttpStatus.OK.value())
                .data(storeCheckResponseDto)
                .build());
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/{storeId}/menus/valid")
    public ResponseEntity<CommonDto<StoreMenuValidResponseDto>> getStoreMenuValid(
            @Valid @Size(min = 1, max = 50) @PathVariable("storeId") String storeId,
            @RequestParam("data") StoreMenuValidRequestDto requestDto) {
        StoreMenuValidResponseDto responseDto = storeAppService.getStoreMenuValid(storeId, requestDto);
        return ResponseEntity.ok(CommonDto.<StoreMenuValidResponseDto>builder()
                .message(messageUtil.getSuccessMessage())
                .code(HttpStatus.OK.value())
                .data(responseDto)
                .build());
    }
}
