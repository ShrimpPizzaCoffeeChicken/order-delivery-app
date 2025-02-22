package com.fortest.orderdelivery.app.domain.image.controller;

import com.fortest.orderdelivery.app.domain.image.dto.MenuImageRequestDto;
import com.fortest.orderdelivery.app.domain.image.dto.ImageResponseDto;
import com.fortest.orderdelivery.app.domain.image.service.ImageService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import java.util.List;

import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/menus")
    public ResponseEntity<CommonDto<ImageResponseDto>> registerMenuImage(
        @RequestParam(value = "image-list", required = false)
        List<MultipartFile> multipartFileList) {
        ImageResponseDto responseDto = imageService.registerMenuImage(multipartFileList);

        return ResponseEntity.ok(CommonDto.<ImageResponseDto>builder()
            .message(messageUtil.getSuccessMessage())
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }

    @PostMapping("/menus/{menuId}")
    public ResponseEntity<CommonDto<ImageResponseDto>> updateMenuImage(
        @RequestParam(value = "image-list", required = false) List<MultipartFile> multipartFileList,
        @PathVariable("menuId") String menuId) {
        ImageResponseDto responseDto = imageService.updateMenuImage(multipartFileList, menuId);

        return ResponseEntity.ok(CommonDto.<ImageResponseDto>builder()
            .message(messageUtil.getSuccessMessage())
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }

    @PostMapping("/options/{optionId}")
    public ResponseEntity<CommonDto<ImageResponseDto>> updateMenuOptionImage(
        @RequestParam(value = "image-list", required = false) List<MultipartFile> multipartFileList,
        @PathVariable("optionId") String menuOptionId) {
        ImageResponseDto responseDto = imageService.updateMenuOptionImage(multipartFileList, menuOptionId);

        return ResponseEntity.ok(CommonDto.<ImageResponseDto>builder()
            .message(messageUtil.getSuccessMessage())
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }

    @PatchMapping("/menus")
    public ResponseEntity<CommonDto<ImageResponseDto>> deleteImageFromS3(@RequestBody
    MenuImageRequestDto menuImageRequestDto) {
        ImageResponseDto responseDto = imageService.deleteImageOnUpdate(menuImageRequestDto);

        return ResponseEntity.ok(CommonDto.<ImageResponseDto>builder()
            .message(messageUtil.getSuccessMessage())
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }
}
