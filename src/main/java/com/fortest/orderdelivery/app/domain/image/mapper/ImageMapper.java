package com.fortest.orderdelivery.app.domain.image.mapper;

import com.fortest.orderdelivery.app.domain.image.dto.MenuImageResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuImageMappingResponseDto;
import java.util.List;

//추후에 Security Filter 생성 후, createBy 등 사용자 정보 넣어주기
public class ImageMapper {

    public static MenuImageResponseDto toMenuImageResponseDto(List<String> imageIdList) {
        return MenuImageResponseDto.builder()
            .imageIdList(imageIdList)
            .build();
    }

    public static MenuImageMappingResponseDto toMenuImageMappingResponseDto(List<String> imageIdList, Boolean result) {
        return MenuImageMappingResponseDto.builder()
            .result(result)
            .imageIdList(imageIdList)
            .build();
    }
}
