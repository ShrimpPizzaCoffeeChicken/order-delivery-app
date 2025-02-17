package com.fortest.orderdelivery.app.domain.area.mapper;

import com.fortest.orderdelivery.app.domain.area.dto.AreaCreateRequestDto;
import com.fortest.orderdelivery.app.domain.area.entity.Area;

public class AreaMapper {

    public static Area createDtoToEntity (AreaCreateRequestDto createDto) {
        return Area.builder()
                .city(createDto.getCity())
                .district(createDto.getDistrict())
                .street(createDto.getStreet())
                .build();
    }
}
