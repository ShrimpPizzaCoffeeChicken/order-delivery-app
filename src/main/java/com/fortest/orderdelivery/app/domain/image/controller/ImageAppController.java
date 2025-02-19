package com.fortest.orderdelivery.app.domain.image.controller;

import com.fortest.orderdelivery.app.domain.image.service.ImageAppService;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuImageMappingRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuImageMappingResponseDto;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/app/images")
@RequiredArgsConstructor
public class ImageAppController {

    private final ImageAppService imageAppService;

    @PatchMapping("/menus")
    public ResponseEntity<CommonDto<MenuImageMappingResponseDto>> updateMenuId(@RequestBody
    MenuImageMappingRequestDto menuImageRequestDto) {

        MenuImageMappingResponseDto menuImageMappingResponseDto = imageAppService.updateMenuId(
            menuImageRequestDto);

        return ResponseEntity.ok(CommonDto.<MenuImageMappingResponseDto>builder()
            .message("메뉴 Id update 완료")
            .code(HttpStatus.OK.value())
            .data(menuImageMappingResponseDto)
            .build());
    }
}
