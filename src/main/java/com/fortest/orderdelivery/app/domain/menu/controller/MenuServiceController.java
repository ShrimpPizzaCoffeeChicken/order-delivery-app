package com.fortest.orderdelivery.app.domain.menu.controller;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuGetResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuListGetRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuListGetResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuListSearchRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuSaveRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuUpdateRequestDto;
import com.fortest.orderdelivery.app.domain.menu.service.MenuService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.security.UserDetailsImpl;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service/menus")
@RequiredArgsConstructor
public class MenuServiceController {

    private final MessageUtil messageUtil;
    private final MenuService menuService;

    @PreAuthorize("hasRole('OWNER') or hasRole('MANAGER') or hasRole('MASTER')")
    @PostMapping
    public ResponseEntity<CommonDto<MenuResponseDto>> saveMenu(
        @Valid @RequestBody MenuSaveRequestDto menuSaveRequestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        MenuResponseDto responseDto = menuService.saveMenu(menuSaveRequestDto,
            userDetails.getUser());

        return ResponseEntity.ok(CommonDto.<MenuResponseDto>builder()
            .message(messageUtil.getSuccessMessage())
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }

    @GetMapping
    public ResponseEntity<CommonDto<MenuListGetResponseDto>> getMenuList(
        @Valid MenuListGetRequestDto menuListGetRequestDto
    ) {
        MenuListGetResponseDto responseDto = menuService.getMenuList(menuListGetRequestDto);

        return ResponseEntity.ok(CommonDto.<MenuListGetResponseDto>builder()
            .message(messageUtil.getSuccessMessage())
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }

    @PreAuthorize("hasRole('OWNER') or hasRole('MANAGER') or hasRole('MASTER')")
    @PatchMapping("/{menuId}")
    public ResponseEntity<CommonDto<MenuResponseDto>> updateMenu(
        @Valid @RequestBody MenuUpdateRequestDto menuUpdateRequestDto,
        @PathVariable("menuId") String menuId,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        MenuResponseDto responseDto = menuService.updateMenu(menuUpdateRequestDto, menuId,
            userDetails.getUser());

        return ResponseEntity.ok(CommonDto.<MenuResponseDto>builder()
            .message(messageUtil.getSuccessMessage())
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }

    @GetMapping("/search")
    public ResponseEntity<CommonDto<MenuListGetResponseDto>> searchMenuList(
        @Valid MenuListSearchRequestDto menuListSearchRequestDto
    ) {
        MenuListGetResponseDto responseDto = menuService.searchMenuList(menuListSearchRequestDto);

        return ResponseEntity.ok(CommonDto.<MenuListGetResponseDto>builder()
            .message(messageUtil.getSuccessMessage())
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }

    @PreAuthorize("hasRole('OWNER') or hasRole('MANAGER') or hasRole('MASTER')")
    @DeleteMapping("/{menuId}")
    public ResponseEntity<CommonDto<MenuResponseDto>> deleteMenu(
        @PathVariable("menuId") String menuId,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        MenuResponseDto responseDto = menuService.deleteMenu(menuId, userDetails.getUser());

        return ResponseEntity.ok(CommonDto.<MenuResponseDto>builder()
            .message(messageUtil.getSuccessMessage())
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }

    @GetMapping("/{menuId}/details")
    public ResponseEntity<CommonDto<MenuGetResponseDto>> getMenu(
        @PathVariable("menuId") String menuId
    ) {
        MenuGetResponseDto responseDto = menuService.getMenu(menuId);

        return ResponseEntity.ok(CommonDto.<MenuGetResponseDto>builder()
            .message(messageUtil.getSuccessMessage())
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }

}
