package com.fortest.orderdelivery.app.domain.menu.controller;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionSaveResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionUpdateRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionsSaveRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuSaveResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuUpdateRequestDto;
import com.fortest.orderdelivery.app.domain.menu.service.MenuOptionService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service")
@RequiredArgsConstructor
public class MenuOptionServiceController {

    private final MenuOptionService menuOptionService;

    @PostMapping("/menus/{menuId}/options")
    public ResponseEntity<CommonDto<MenuOptionSaveResponseDto>> saveMenuOption(
        @RequestBody MenuOptionsSaveRequestDto menuOptionsSaveRequestDto,
        @PathVariable("menuId") String menuId
    ) {
        MenuOptionSaveResponseDto responseDto = menuOptionService.saveMenuOption(
            menuOptionsSaveRequestDto, menuId);

        return ResponseEntity.ok(CommonDto.<MenuOptionSaveResponseDto>builder()
            .message("메뉴 옵션 등록 완료")
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }

    @PatchMapping("/options/{optionId}")
    public ResponseEntity<CommonDto<MenuOptionSaveResponseDto>> updateMenuOption(
        @RequestBody MenuOptionUpdateRequestDto menuOptionUpdateRequestDto,
        @PathVariable("optionId") String optionId
    ) {
        MenuOptionSaveResponseDto responseDto = menuOptionService.updateMenuOption(menuOptionUpdateRequestDto, optionId);

        return ResponseEntity.ok(CommonDto.<MenuOptionSaveResponseDto>builder()
            .message("메뉴 옵션 수정 완료")
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }
}
