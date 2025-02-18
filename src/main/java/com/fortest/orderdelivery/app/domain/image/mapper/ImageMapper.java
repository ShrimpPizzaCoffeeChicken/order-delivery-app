package com.fortest.orderdelivery.app.domain.image.mapper;

import com.fortest.orderdelivery.app.domain.image.dto.MenuImageResponseDto;
import java.util.List;

//추후에 Security Filter 생성 후, createBy 등 사용자 정보 넣어주기
public class ImageMapper {

    public static MenuImageResponseDto toMenuImageSaveResponseDto(List<String> imageIdList) {
        return MenuImageResponseDto.builder()
            .imageIdList(imageIdList)
            .build();
    }
}
