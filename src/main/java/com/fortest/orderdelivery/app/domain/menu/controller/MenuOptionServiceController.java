package com.fortest.orderdelivery.app.domain.menu.controller;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionUpdateRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionsSaveRequestDto;
import com.fortest.orderdelivery.app.domain.menu.service.MenuOptionService;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
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

    private final MessageUtil messageUtil;
    private final MenuOptionService menuOptionService;

    @PostMapping("/menus/{menuId}/options")
    public ResponseEntity<CommonDto<MenuOptionResponseDto>> saveMenuOption(
        @RequestBody MenuOptionsSaveRequestDto menuOptionsSaveRequestDto,
        @PathVariable("menuId") String menuId
    ) {
        MenuOptionResponseDto responseDto = menuOptionService.saveMenuOption(
            menuOptionsSaveRequestDto, menuId);

        return ResponseEntity.ok(CommonDto.<MenuOptionResponseDto>builder()
            .message(messageUtil.getSuccessMessage())
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }

    @PatchMapping("/options/{optionId}")
    public ResponseEntity<CommonDto<MenuOptionResponseDto>> updateMenuOption(
        @RequestBody MenuOptionUpdateRequestDto menuOptionUpdateRequestDto,
        @PathVariable("optionId") String optionId
    ) {
        MenuOptionResponseDto responseDto = menuOptionService.updateMenuOption(menuOptionUpdateRequestDto, optionId, new User());

        return ResponseEntity.ok(CommonDto.<MenuOptionResponseDto>builder()
            .message(messageUtil.getSuccessMessage())
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }

    @DeleteMapping("/options/{optionId}")
    public ResponseEntity<CommonDto<MenuOptionResponseDto>> deleteMenuOption(
        @PathVariable("optionId") String optionId
    ) {
        MenuOptionResponseDto responseDto = menuOptionService.deleteMenuOption(optionId, new User());

        return ResponseEntity.ok(CommonDto.<MenuOptionResponseDto>builder()
            .message(messageUtil.getSuccessMessage())
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }
}
