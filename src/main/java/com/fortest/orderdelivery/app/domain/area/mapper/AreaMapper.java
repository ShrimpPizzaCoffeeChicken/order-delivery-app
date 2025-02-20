package com.fortest.orderdelivery.app.domain.area.mapper;

import com.fortest.orderdelivery.app.domain.area.dto.AreaGetListResponseDto;
import com.fortest.orderdelivery.app.domain.area.dto.AreaSaveRequestDto;
import com.fortest.orderdelivery.app.domain.area.entity.Area;

import java.util.ArrayList;
import java.util.List;

public class AreaMapper {

    public static Area saveDtoToEntity(AreaSaveRequestDto createDto) {
        return Area.builder()
                .city(createDto.getCity())
                .district(createDto.getDistrict())
                .street(createDto.getStreet())
                .build();
    }

    public static AreaGetListResponseDto entityListToGetListResponseDto(List<Area> areaList) {
        ArrayList<AreaGetListResponseDto.AreaDto> areaDtoList = new ArrayList<>();
        for (Area area : areaList) {
            areaDtoList.add(
                AreaGetListResponseDto.AreaDto.builder()
                        .id(area.getId())
                        .city(area.getCity())
                        .district(area.getDistrict())
                        .street(area.getStreet())
                        .build()
            );
        }
        return AreaGetListResponseDto.builder()
                .areaList(areaDtoList)
                .build();
    }
}
