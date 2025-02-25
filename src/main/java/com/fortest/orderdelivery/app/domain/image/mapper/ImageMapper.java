package com.fortest.orderdelivery.app.domain.image.mapper;

import com.fortest.orderdelivery.app.domain.image.dto.ImageResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuImageMappingResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionImageMappingResponseDto;
import java.util.List;

//추후에 Security Filter 생성 후, createBy 등 사용자 정보 넣어주기
public class ImageMapper {

    public static ImageResponseDto toImageResponseDto(List<String> imageIdList) {
        return ImageResponseDto.builder()
            .imageIdList(imageIdList)
            .build();
    }

    public static MenuImageMappingResponseDto toMenuImageMappingResponseDto(List<String> imageIdList, Boolean result) {
        return MenuImageMappingResponseDto.builder()
            .result(result)
            .imageIdList(imageIdList)
            .build();
    }

    public static MenuOptionImageMappingResponseDto toMenuOptionImageMappingResponseDto(List<String> imageIdList, boolean result) {
        return MenuOptionImageMappingResponseDto.builder()
            .result(result)
            .imageIdList(imageIdList)
            .build();
    }
}
