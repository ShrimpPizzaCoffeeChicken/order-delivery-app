package com.fortest.orderdelivery.app.domain.store.controller;

import com.fortest.orderdelivery.app.domain.store.dto.*;
import com.fortest.orderdelivery.app.domain.store.service.StoreService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.security.UserDetailsImpl;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Validated
@RequestMapping("/api/service")
@RequiredArgsConstructor
public class StoreServiceController {

    private final MessageUtil messageUtil;
    private final StoreService storeService;

    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/stores/{storeId}/categories")
    public ResponseEntity<CommonDto<StoreUpdateCategoryResponseDto>> updateCategory(@PathVariable("storeId") String storeId,
                                                                                    @RequestBody StoreUpdateCategoryRequestDto requestDto,
                                                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {
        StoreUpdateCategoryResponseDto responseDto = storeService.updateCategory(storeId, userDetails.getUser(), requestDto);
        return ResponseEntity.ok(
                CommonDto.<StoreUpdateCategoryResponseDto>builder()
                        .message(messageUtil.getSuccessMessage())
                        .code(HttpStatus.OK.value())
                        .data(responseDto)
                        .build()
        );
    }

    @GetMapping("/stores/search")
    public ResponseEntity<CommonDto<StoreSearchResponseDto>> searchStore(
            @Nullable @RequestParam("category-id") String categoryId,
            @Valid StoreSearchRequestDto requestDto
    ) {
        StoreSearchResponseDto storeSearchResponseDto = storeService.searchStore(
                requestDto.getPage(),
                requestDto.getSize(),
                requestDto.getOrderby(),
                requestDto.getSort(),
                requestDto.getSearch(),
                categoryId,
                requestDto.getCity(),
                requestDto.getDistrict(),
                requestDto.getStreet()
        );

        return ResponseEntity.ok(
                CommonDto.<StoreSearchResponseDto>builder()
                        .message(messageUtil.getSuccessMessage())
                        .code(HttpStatus.OK.value())
                        .data(storeSearchResponseDto)
                        .build()
        );
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/stores")
    public ResponseEntity<CommonDto<StoreSaveResponseDto>> saveStore(@Valid @RequestBody StoreSaveRequestDto storeSaveRequestDto,
                                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        StoreSaveResponseDto storeSaveResponseDto = storeService.saveStore(storeSaveRequestDto, userDetails.getUser());

        return ResponseEntity.ok(
                CommonDto.<StoreSaveResponseDto>builder()
                        .message(messageUtil.getSuccessMessage())
                        .code(HttpStatus.OK.value())
                        .data(storeSaveResponseDto)
                        .build()
        );
    }

    @GetMapping("/stores/{storeId}/detail")
    public ResponseEntity<CommonDto<StoreGetDetailResponseDto>> getStoreDetail (@PathVariable("storeId") String storeId,
                                                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        StoreGetDetailResponseDto storeDetailResponseDto = storeService.getStoreDetail(storeId, userDetails.getUser());
        return ResponseEntity.ok(
                CommonDto.<StoreGetDetailResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(storeDetailResponseDto)
                        .build()
        );
    }

    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/stores/{storeId}")
    public ResponseEntity<CommonDto<StoreUpdateResponseDto>> updateStore(@PathVariable String storeId,
                                                                         @RequestBody StoreUpdateRequestDto storeUpdateRequestDto,
                                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        StoreUpdateResponseDto storeUpdateResponseDto = storeService.updateStore(storeId, storeUpdateRequestDto, userDetails.getUser());

        return ResponseEntity.ok(
                CommonDto.<StoreUpdateResponseDto>builder()
                        .message(messageUtil.getSuccessMessage())
                        .code(HttpStatus.OK.value())
                        .data(storeUpdateResponseDto)
                        .build()
        );
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/stores/{storeId}")
    public ResponseEntity<CommonDto<StoreDeleteResponseDto>> deleteStore(@PathVariable String storeId,
                                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        StoreDeleteResponseDto storeDeleteResponseDto = storeService.deleteStore(storeId, userDetails.getUser());

        return ResponseEntity.ok(
                CommonDto.<StoreDeleteResponseDto>builder()
                        .message(messageUtil.getSuccessMessage())
                        .code(HttpStatus.OK.value())
                        .data(storeDeleteResponseDto)
                        .build()
        );
    }
}
