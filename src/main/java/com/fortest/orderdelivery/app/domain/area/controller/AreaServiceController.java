package com.fortest.orderdelivery.app.domain.area.controller;

import com.fortest.orderdelivery.app.domain.area.dto.AreaGetListResponseDto;
import com.fortest.orderdelivery.app.domain.area.dto.AreaSaveRequestDto;
import com.fortest.orderdelivery.app.domain.area.dto.AreaSaveResponseDto;
import com.fortest.orderdelivery.app.domain.area.entity.Area;
import com.fortest.orderdelivery.app.domain.area.service.AreaService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/service")
@RestController
@RequiredArgsConstructor
public class AreaServiceController {

    private final AreaService areaService;

    @PostMapping("/areas")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CommonDto<AreaSaveResponseDto>> saveArea(@RequestBody AreaSaveRequestDto createDto,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new BusinessLogicException("로그인한 사용자 정보가 없습니다.");
        }
        Long userId = userDetails.getUserId(); // 🔥 유저의 ID 가져오기
        // TODO : 회원 ID 획득 해야함
        Area area = areaService.saveArea(createDto, 123L);
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

    @GetMapping("/areas")
    public ResponseEntity<CommonDto<AreaGetListResponseDto>> getAreaList() {
        AreaGetListResponseDto areaList = areaService.getAreaList();

        return ResponseEntity.ok(
                CommonDto.<AreaGetListResponseDto> builder()
                        .message("SUCCESS")
                        .code(HttpStatus.OK.value())
                        .data(areaList)
                        .build()
        );
    }
}
