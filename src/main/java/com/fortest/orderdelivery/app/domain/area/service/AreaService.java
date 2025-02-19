package com.fortest.orderdelivery.app.domain.area.service;

import com.fortest.orderdelivery.app.domain.area.dto.AreaGetListResponseDto;
import com.fortest.orderdelivery.app.domain.area.dto.AreaSaveRequestDto;
import com.fortest.orderdelivery.app.domain.area.entity.Area;
import com.fortest.orderdelivery.app.domain.area.mapper.AreaMapper;
import com.fortest.orderdelivery.app.domain.area.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AreaService {

    private final AreaRepository areaRepository;

    /**
     * 지역 생성
     * @param saveDto
     * @return 생성된 Area Entity
     */
    public Area saveArea(AreaSaveRequestDto saveDto, Long userId) {
        Area area = AreaMapper.saveDtoToEntity(saveDto);
        area.isCreatedBy(userId);
        return areaRepository.save(area);
    }

    public AreaGetListResponseDto getAreaList () {
        List<Area> areas = areaRepository.findAll();
        return AreaMapper.entityListToGetListResponseDto(areas);
    }
}
