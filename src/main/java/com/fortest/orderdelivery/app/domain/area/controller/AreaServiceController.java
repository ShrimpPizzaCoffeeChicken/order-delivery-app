package com.fortest.orderdelivery.app.domain.area.controller;

import com.fortest.orderdelivery.app.domain.area.dto.AreaSaveRequestDto;
import com.fortest.orderdelivery.app.domain.area.dto.AreaSaveResponseDto;
import com.fortest.orderdelivery.app.domain.area.entity.Area;
import com.fortest.orderdelivery.app.domain.area.service.AreaService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<CommonDto<AreaSaveResponseDto>> saveArea(@RequestBody AreaSaveRequestDto createDto) {
        Area area = areaService.saveArea(createDto);
        AreaSaveResponseDto responseDto = AreaSaveResponseDto.builder()
                .id(area.getId())
                .city(area.getCity())
                .district(area.getDistrict())
                .street(area.getStreet())
                .build();

        return ResponseEntity.ok(
                CommonDto.<AreaSaveResponseDto> builder()
                        .message("SUCCESS")
                        .code(HttpStatus.OK.value())
                        .data(responseDto)
                        .build()
                );
    }
}
