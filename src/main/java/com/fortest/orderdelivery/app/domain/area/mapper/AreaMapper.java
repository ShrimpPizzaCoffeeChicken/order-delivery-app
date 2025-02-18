package com.fortest.orderdelivery.app.domain.area.mapper;

import com.fortest.orderdelivery.app.domain.area.dto.AreaSaveRequestDto;
import com.fortest.orderdelivery.app.domain.area.dto.AreaSaveResponseDto;
import com.fortest.orderdelivery.app.domain.area.entity.Area;

public class AreaMapper {

    public static Area saveDtoToEntity(AreaSaveRequestDto createDto) {
        return Area.builder()
                .city(createDto.getCity())
                .district(createDto.getDistrict())
                .street(createDto.getStreet())
                .build();
    }

    public static AreaSaveResponseDto entityToSaveResponseDto(Area area) {
        return AreaSaveResponseDto.builder()
                .city(area.getCity())
                .district(area.getDistrict())
                .street(area.getStreet())
                .build();
    }
}
