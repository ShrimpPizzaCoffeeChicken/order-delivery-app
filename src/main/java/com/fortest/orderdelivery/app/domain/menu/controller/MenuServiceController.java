package com.fortest.orderdelivery.app.domain.menu.controller;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuListGetResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuSaveRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuSaveResponseDto;
import com.fortest.orderdelivery.app.domain.menu.service.MenuService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service/menus")
@RequiredArgsConstructor
public class MenuServiceController {
    private final MenuService menuService;

    @PostMapping
    public ResponseEntity<CommonDto<MenuSaveResponseDto>> saveMenu(@RequestBody MenuSaveRequestDto menuSaveRequestDto) {
        MenuSaveResponseDto responseDto = menuService.saveMenu(menuSaveRequestDto);

        return ResponseEntity.ok(CommonDto.<MenuSaveResponseDto>builder()
            .message("메뉴 등록 완료")
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }

    @GetMapping
    public ResponseEntity<CommonDto<MenuListGetResponseDto>> getMenuList(
        @RequestParam("store-id") String storeId,
        @RequestParam("page") int page,
        @RequestParam("size") int size,
        @RequestParam("order-by") String orderBy,
        @RequestParam("sort") String sort
    ) {
        MenuListGetResponseDto responseDto = menuService.getMenuList(storeId, page, size, orderBy, sort);

        return ResponseEntity.ok(CommonDto.<MenuListGetResponseDto>builder()
            .message("메뉴 리스트 조회 완료")
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }

}
