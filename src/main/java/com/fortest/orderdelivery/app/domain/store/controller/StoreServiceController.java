package com.fortest.orderdelivery.app.domain.store.controller;

import com.fortest.orderdelivery.app.domain.store.dto.*;
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

    @GetMapping("/stores/search")
    public ResponseEntity<CommonDto<StoreSearchResponseDto>> searchStore(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam("orderby") String orderby,
            @RequestParam("sort") String sort,
            @RequestParam("search") String search,
            @RequestParam("category-id") String categoryId,
            @RequestParam("city") String city,
            @RequestParam("district") String district,
            @RequestParam("street") String street
    ) {
        StoreSearchResponseDto storeSearchResponseDto = storeService.searchStore(page, size, orderby, sort, search, categoryId, city, district, street);
        return ResponseEntity.ok(
                CommonDto.<StoreSearchResponseDto>builder()
                        .message("SUCCESS")
                        .code(HttpStatus.OK.value())
                        .data(storeSearchResponseDto)
                        .build()
        );
    }

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

    @GetMapping("/stores/{storeId}")
    public ResponseEntity<CommonDto<StoreGetDetailResponseDto>> getStoreDetail (@PathVariable("storeId") String storeId) {
        StoreGetDetailResponseDto storeDetailResponseDto = storeService.getStoreDetail(storeId);

        return ResponseEntity.ok(
                CommonDto.<StoreGetDetailResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message("Success")
                        .data(storeDetailResponseDto)
                        .build()
        );
    }

    @PatchMapping("/stores/{storeId}")
    public ResponseEntity<CommonDto<StoreUpdateResponseDto>> updateStore(@PathVariable String storeId, @RequestBody StoreUpdateRequestDto storeUpdateRequestDto){
        StoreUpdateResponseDto storeUpdateResponseDto = storeService.updateStore(storeId, storeUpdateRequestDto);

        return ResponseEntity.ok(
                CommonDto.<StoreUpdateResponseDto>builder()
                        .message("SUCCESS")
                        .code(HttpStatus.OK.value())
                        .data(storeUpdateResponseDto)
                        .build()
        );
    }

    @DeleteMapping("/stores/{storeId}")
    public ResponseEntity<CommonDto<StoreDeleteResponseDto>> deleteStore(@PathVariable String storeId){
        StoreDeleteResponseDto storeDeleteResponseDto = storeService.deleteStore(storeId, 123L);

        return ResponseEntity.ok(
                CommonDto.<StoreDeleteResponseDto>builder()
                        .message("SUCCESS")
                        .code(HttpStatus.OK.value())
                        .data(storeDeleteResponseDto)
                        .build()
        );
    }
}
