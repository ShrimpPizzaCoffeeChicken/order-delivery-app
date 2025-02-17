package com.fortest.orderdelivery.app.domain.area.controller;

import com.fortest.orderdelivery.app.domain.area.dto.AreaCreateRequestDto;
import com.fortest.orderdelivery.app.domain.area.dto.AreaCreateResponseDto;
import com.fortest.orderdelivery.app.domain.area.entity.Area;
import com.fortest.orderdelivery.app.domain.area.service.AreaService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/service")
@RestController
@RequiredArgsConstructor
public class AreaServiceController {

    private final AreaService areaService;

    @PostMapping("/areas")
    public CommonDto<AreaCreateResponseDto> createArea(@RequestBody AreaCreateRequestDto createDto) {
        Area area = areaService.createArea(createDto);
        AreaCreateResponseDto responseDto = AreaCreateResponseDto.builder()
                .id(area.getId())
                .city(area.getCity())
                .district(area.getDistrict())
                .street(area.getStreet())
                .build();
        return new CommonDto<>("SUCCESS", HttpStatus.OK.value(), responseDto);
    }
}
