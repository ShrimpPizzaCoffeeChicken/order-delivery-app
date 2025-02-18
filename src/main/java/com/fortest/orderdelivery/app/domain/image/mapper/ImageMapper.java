package com.fortest.orderdelivery.app.domain.image.mapper;

import com.fortest.orderdelivery.app.domain.image.dto.MenuImageSaveResponseDto;
import com.fortest.orderdelivery.app.domain.image.entity.Image;
import java.util.List;

//추후에 Security Filter 생성 후, createBy 등 사용자 정보 넣어주기
public class ImageMapper {

    public static MenuImageSaveResponseDto toMenuImageSaveResponseDto(List<String> imageIdList) {
        return MenuImageSaveResponseDto.builder()
            .imageIdList(imageIdList)
            .build();
    }
}
