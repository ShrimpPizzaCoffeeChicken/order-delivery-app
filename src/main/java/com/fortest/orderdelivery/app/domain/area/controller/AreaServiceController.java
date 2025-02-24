package com.fortest.orderdelivery.app.domain.area.controller;

import com.fortest.orderdelivery.app.domain.area.dto.AreaGetListResponseDto;
import com.fortest.orderdelivery.app.domain.area.dto.AreaSaveRequestDto;
import com.fortest.orderdelivery.app.domain.area.dto.AreaSaveResponseDto;
import com.fortest.orderdelivery.app.domain.area.entity.Area;
import com.fortest.orderdelivery.app.domain.area.service.AreaService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.security.UserDetailsImpl;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/service")
@Validated
@RestController
@RequiredArgsConstructor
public class AreaServiceController {

    private final MessageUtil messageUtil;
    private final AreaService areaService;

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/areas")
    public ResponseEntity<CommonDto<AreaSaveResponseDto>> saveArea(@Valid @RequestBody AreaSaveRequestDto createDto,
                                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Area area = areaService.saveArea(createDto, userDetails.getUser());
        AreaSaveResponseDto responseDto = AreaSaveResponseDto.builder()
                .id(area.getId())
                .city(area.getCity())
                .district(area.getDistrict())
                .street(area.getStreet())
                .build();

        return ResponseEntity.ok(
                CommonDto.<AreaSaveResponseDto> builder()
                        .message(messageUtil.getSuccessMessage())
                        .code(HttpStatus.OK.value())
                        .data(responseDto)
                        .build()
        );
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/areas")
    public ResponseEntity<CommonDto<AreaGetListResponseDto>> getAreaList() {
        AreaGetListResponseDto areaList = areaService.getAreaList();

        return ResponseEntity.ok(
                CommonDto.<AreaGetListResponseDto> builder()
                        .message(messageUtil.getSuccessMessage())
                        .code(HttpStatus.OK.value())
                        .data(areaList)
                        .build()
        );
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/areas/{areaId}")
    public ResponseEntity<CommonDto<Map<String, String>>> deleteArea(@PathVariable("areaId") String areaId,
                                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String deletedAreaId = areaService.deleteArea(areaId, userDetails.getUser());
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
