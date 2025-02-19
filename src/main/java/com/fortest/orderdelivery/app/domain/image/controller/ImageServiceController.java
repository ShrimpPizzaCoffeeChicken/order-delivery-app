package com.fortest.orderdelivery.app.domain.image.controller;

import com.fortest.orderdelivery.app.domain.image.dto.MenuImageRequestDto;
import com.fortest.orderdelivery.app.domain.image.dto.MenuImageResponseDto;
import com.fortest.orderdelivery.app.domain.image.service.ImageService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import java.util.List;
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

    private final ImageService imageService;

    @PostMapping("/menus")
    public ResponseEntity<CommonDto<MenuImageResponseDto>> registerMenuImage(
        @RequestParam(value = "image-list", required = false)
        List<MultipartFile> multipartFileList) {
        MenuImageResponseDto responseDto = imageService.registerMenuImage(multipartFileList);

        return ResponseEntity.ok(CommonDto.<MenuImageResponseDto>builder()
            .message("사진 저장 완료")
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }

    @PostMapping("/menus/{menuId}")
    public ResponseEntity<CommonDto<MenuImageResponseDto>> updateMenuImage(
        @RequestParam(value = "image-list", required = false) List<MultipartFile> multipartFileList,
        @PathVariable("menuId") String menuId) {
        MenuImageResponseDto responseDto = imageService.updateMenuImage(multipartFileList, menuId);

        return ResponseEntity.ok(CommonDto.<MenuImageResponseDto>builder()
            .message("사진 추가 저장 완료")
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }

    @PatchMapping("/menus")
    public ResponseEntity<CommonDto<MenuImageResponseDto>> deleteImageFromS3(@RequestBody
    MenuImageRequestDto menuImageRequestDto) {
        MenuImageResponseDto responseDto = imageService.deleteImageFromS3(menuImageRequestDto);

        return ResponseEntity.ok(CommonDto.<MenuImageResponseDto>builder()
            .message("사진 삭제 완료")
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }
}
