package com.fortest.orderdelivery.app.domain.image.controller;

import com.fortest.orderdelivery.app.domain.image.dto.ImageResponseDto;
import com.fortest.orderdelivery.app.domain.image.service.ImageAppService;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuImageMappingRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuImageMappingResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionImageMappingRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionImageMappingResponseDto;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.security.UserDetailsImpl;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/app/images")
@RequiredArgsConstructor
public class ImageAppController {

    private final MessageUtil messageUtil;
    private final ImageAppService imageAppService;

    @PatchMapping("/menus")
    public ResponseEntity<CommonDto<MenuImageMappingResponseDto>> updateMenuId(
        @RequestBody MenuImageMappingRequestDto menuImageRequestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        MenuImageMappingResponseDto menuImageMappingResponseDto = imageAppService.updateMenuId(
            menuImageRequestDto, userDetails.getUser());

        return ResponseEntity.ok(CommonDto.<MenuImageMappingResponseDto>builder()
            .message(messageUtil.getSuccessMessage())
            .code(HttpStatus.OK.value())
            .data(menuImageMappingResponseDto)
            .build());
    }

    @PatchMapping("/options")
    public ResponseEntity<CommonDto<MenuOptionImageMappingResponseDto>> updateMenuOptionId(
        @RequestBody MenuOptionImageMappingRequestDto menuOptionImageRequestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        MenuOptionImageMappingResponseDto menuOptionImageMappingResponseDto = imageAppService.updateMenuOptionId(
            menuOptionImageRequestDto, userDetails.getUser());

        return ResponseEntity.ok(CommonDto.<MenuOptionImageMappingResponseDto>builder()
            .message(messageUtil.getSuccessMessage())
            .code(HttpStatus.OK.value())
            .data(menuOptionImageMappingResponseDto)
            .build());
    }

    @DeleteMapping("/options/{optionId}")
    public ResponseEntity<CommonDto<ImageResponseDto>> deleteImageOnMenuOptionDelete(
        @PathVariable("optionId") String optionId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ImageResponseDto responseDto = imageAppService.deleteImageOnMenuOptionDelete(optionId,
            userDetails.getUser());

        return ResponseEntity.ok(CommonDto.<ImageResponseDto>builder()
            .message(messageUtil.getSuccessMessage())
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }

    @DeleteMapping("/menus/{menuId}")
    public ResponseEntity<CommonDto<ImageResponseDto>> deleteImageOnMenuDelete(
        @PathVariable("menuId") String menuId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ImageResponseDto responseDto = imageAppService.deleteImageOnMenuDelete(menuId,
            userDetails.getUser());

        return ResponseEntity.ok(CommonDto.<ImageResponseDto>builder()
            .message(messageUtil.getSuccessMessage())
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }
}
