package com.fortest.orderdelivery.app.domain.menu.controller;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionAppResponseDto;
import com.fortest.orderdelivery.app.domain.menu.service.MenuOptionAppService;
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
@RequestMapping("/api/app/menus/options")
@RequiredArgsConstructor
public class MenuOptionAppController {

    private final MenuOptionAppService menuOptionAppService;

    @GetMapping
    public ResponseEntity<CommonDto<MenuOptionAppResponseDto>> getMenuOptionFromApp(
        @RequestParam(name = "menuOptionId") List<String> menuOptionIdList) {
        MenuOptionAppResponseDto response = menuOptionAppService.getMenuOptionFromApp(menuOptionIdList);

        return ResponseEntity.ok(CommonDto.<MenuOptionAppResponseDto>builder()
            .message("메뉴 옵션 객체 응답 완료")
            .code(HttpStatus.OK.value())
            .data(response)
            .build());
    }
}
