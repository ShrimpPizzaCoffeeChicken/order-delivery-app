package com.fortest.orderdelivery.app.domain.menu.controller;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuSaveRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuSaveResponseDto;
import com.fortest.orderdelivery.app.domain.menu.service.MenuService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service/menus")
@RequiredArgsConstructor
public class MenuServiceController {
    private final MenuService menuService;

    @PostMapping
    public ResponseEntity<?> saveMenu(@RequestBody MenuSaveRequestDto menuSaveRequestDto) {
        MenuSaveResponseDto responseDto = menuService.saveMenu(menuSaveRequestDto);

        return ResponseEntity.ok(CommonDto.builder()
            .message("메뉴 등록 완료")
            .code(200)
            .data(responseDto));
    }
}
