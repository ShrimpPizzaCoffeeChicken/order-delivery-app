package com.fortest.orderdelivery.app.domain.image.controller;

import com.fortest.orderdelivery.app.domain.image.dto.MenuImageSaveResponseDto;
import com.fortest.orderdelivery.app.domain.image.service.ImageService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<CommonDto<MenuImageSaveResponseDto>> updateImageToS3(@RequestParam(value = "image-list", required = false) List<MultipartFile> multipartFileList) {
        MenuImageSaveResponseDto responseDto = imageService.updateImageToS3(multipartFileList);

        return ResponseEntity.ok(CommonDto.<MenuImageSaveResponseDto>builder()
            .message("사진 저장 완료")
            .code(HttpStatus.OK.value())
            .data(responseDto)
            .build());
    }
}
