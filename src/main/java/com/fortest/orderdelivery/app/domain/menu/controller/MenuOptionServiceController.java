package com.fortest.orderdelivery.app.domain.menu.controller;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionUpdateRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionsSaveRequestDto;
import com.fortest.orderdelivery.app.domain.menu.service.MenuOptionService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    public ResponseEntity<CommonDto<MenuOptionResponseDto>> saveMenuOption(
        @RequestBody MenuOptionsSaveRequestDto menuOptionsSaveRequestDto,
        @PathVariable("menuId") String menuId
    ) {
        MenuOptionResponseDto responseDto = menuOptionService.saveMenuOption(
            menuOptionsSaveRequestDto, menuId);

        return ResponseEntity.ok(CommonDto.<MenuOptionResponseDto>builder()
            .message("메뉴 옵션 등록 완료")
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }

    @PatchMapping("/options/{optionId}")
    public ResponseEntity<CommonDto<MenuOptionResponseDto>> updateMenuOption(
        @RequestBody MenuOptionUpdateRequestDto menuOptionUpdateRequestDto,
        @PathVariable("optionId") String optionId
    ) {
        MenuOptionResponseDto responseDto = menuOptionService.updateMenuOption(menuOptionUpdateRequestDto, optionId);

        return ResponseEntity.ok(CommonDto.<MenuOptionResponseDto>builder()
            .message("메뉴 옵션 수정 완료")
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }

    @DeleteMapping("/options/{optionId}")
    public ResponseEntity<CommonDto<MenuOptionResponseDto>> deleteMenuOption(
        @PathVariable("optionId") String optionId
    ) {
        MenuOptionResponseDto responseDto = menuOptionService.deleteMenuOption(optionId);

        return ResponseEntity.ok(CommonDto.<MenuOptionResponseDto>builder()
            .message("메뉴 옵션 삭제 완료")
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }
}
