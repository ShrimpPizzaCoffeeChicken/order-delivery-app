package com.fortest.orderdelivery.app.domain.menu.controller;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuAppResponseDto;
import com.fortest.orderdelivery.app.domain.menu.service.MenuAppService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app/menus")
@RequiredArgsConstructor
public class MenuAppController {

    private final MenuAppService menuAppService;

    @GetMapping
    public ResponseEntity<CommonDto<MenuAppResponseDto>> getMenuFromApp(
        @RequestParam(name = "menuId") List<String> menuIdList) {
        MenuAppResponseDto response = menuAppService.getMenuFromApp(menuIdList);

        return ResponseEntity.ok(CommonDto.<MenuAppResponseDto>builder()
            .message("메뉴 객체 응답 완료")
            .code(HttpStatus.OK.value())
            .data(response)
            .build());
    }

}
