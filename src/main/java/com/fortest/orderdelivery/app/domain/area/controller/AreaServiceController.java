package com.fortest.orderdelivery.app.domain.area.controller;

import com.fortest.orderdelivery.app.domain.area.dto.AreaSaveRequestDto;
import com.fortest.orderdelivery.app.domain.area.dto.AreaSaveResponseDto;
import com.fortest.orderdelivery.app.domain.area.entity.Area;
import com.fortest.orderdelivery.app.domain.area.mapper.AreaMapper;
import com.fortest.orderdelivery.app.domain.area.service.AreaService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/service")
@RestController
@RequiredArgsConstructor
public class AreaServiceController {

    private final AreaService areaService;

    @PostMapping("/areas")
    public ResponseEntity<CommonDto<AreaSaveResponseDto>> saveArea(@RequestBody AreaSaveRequestDto createDto) {
        // TODO : 회원 아이디 획득해야함
        Area area = areaService.saveArea(createDto, 123L);
        AreaSaveResponseDto responseDto = AreaMapper.entityToSaveResponseDto(area);

        return ResponseEntity.ok(
                CommonDto.<AreaSaveResponseDto> builder()
                        .message("SUCCESS")
                        .code(HttpStatus.OK.value())
                        .data(responseDto)
                        .build()
                );
    }

    @DeleteMapping("/areas/{areaId}")
    public ResponseEntity<CommonDto<Map<String, String>>> deleteArea(@PathVariable("areaId") String areaId) {
        // TODO : 회원 아이디 획득해야함
        String deletedAreaId = areaService.deleteArea(areaId, 123L);
        Map<String, String> data = Map.of("area-id", deletedAreaId);

        return ResponseEntity.ok(
                CommonDto.<Map<String, String>> builder()
                        .message("SUCCESS")
                        .code(HttpStatus.OK.value())
                        .data(data)
                        .build()
        );
    }
}
