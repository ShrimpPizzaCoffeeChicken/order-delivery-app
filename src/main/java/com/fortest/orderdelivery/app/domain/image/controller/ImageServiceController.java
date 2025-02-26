package com.fortest.orderdelivery.app.domain.image.controller;

import com.fortest.orderdelivery.app.domain.image.dto.MenuImageRequestDto;
import com.fortest.orderdelivery.app.domain.image.dto.ImageResponseDto;
import com.fortest.orderdelivery.app.domain.image.service.ImageService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.security.UserDetailsImpl;
import java.util.List;

import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/service/images")
@RequiredArgsConstructor
public class ImageServiceController {

    private final MessageUtil messageUtil;
    private final ImageService imageService;

    @PreAuthorize("hasRole('OWNER') or hasRole('MANAGER') or hasRole('MASTER')")
    @PostMapping("/menus")
    public ResponseEntity<CommonDto<ImageResponseDto>> registerMenuImage(
        @RequestParam(value = "image-list", required = false)
        List<MultipartFile> multipartFileList,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ImageResponseDto responseDto = imageService.registerMenuImage(multipartFileList, userDetails.getUser());

        return ResponseEntity.ok(CommonDto.<ImageResponseDto>builder()
            .message(messageUtil.getSuccessMessage())
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }

    @PreAuthorize("hasRole('OWNER') or hasRole('MANAGER') or hasRole('MASTER')")
    @PostMapping("/menus/{menuId}")
    public ResponseEntity<CommonDto<ImageResponseDto>> updateMenuImage(
        @RequestParam(value = "image-list", required = false) List<MultipartFile> multipartFileList,
        @PathVariable("menuId") String menuId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ImageResponseDto responseDto = imageService.updateMenuImage(multipartFileList, menuId, userDetails.getUser());

        return ResponseEntity.ok(CommonDto.<ImageResponseDto>builder()
            .message(messageUtil.getSuccessMessage())
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }

    @PreAuthorize("hasRole('OWNER') or hasRole('MANAGER') or hasRole('MASTER')")
    @PostMapping("/options/{optionId}")
    public ResponseEntity<CommonDto<ImageResponseDto>> updateMenuOptionImage(
        @RequestParam(value = "image-list", required = false) List<MultipartFile> multipartFileList,
        @PathVariable("optionId") String menuOptionId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ImageResponseDto responseDto = imageService.updateMenuOptionImage(multipartFileList, menuOptionId, userDetails.getUser());

        return ResponseEntity.ok(CommonDto.<ImageResponseDto>builder()
            .message(messageUtil.getSuccessMessage())
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }

    @PreAuthorize("hasRole('OWNER') or hasRole('MANAGER') or hasRole('MASTER')")
    @PatchMapping("/menus")
    public ResponseEntity<CommonDto<ImageResponseDto>> deleteImageFromS3(
        @RequestBody MenuImageRequestDto menuImageRequestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ImageResponseDto responseDto = imageService.deleteImageOnUpdate(menuImageRequestDto, userDetails.getUser());

        return ResponseEntity.ok(CommonDto.<ImageResponseDto>builder()
            .message(messageUtil.getSuccessMessage())
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }
}
