package com.fortest.orderdelivery.app.domain.area.service;

import com.fortest.orderdelivery.app.domain.area.dto.AreaCreateRequestDto;
import com.fortest.orderdelivery.app.domain.area.entity.Area;
import com.fortest.orderdelivery.app.domain.area.mapper.AreaMapper;
import com.fortest.orderdelivery.app.domain.area.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AreaService {

    private final AreaRepository areaRepository;

    /**
     * 지역 생성
     * @param createDto
     * @return 생성된 Area Entity
     */
    public Area createArea(AreaCreateRequestDto createDto) {
        Area area = AreaMapper.createDtoToEntity(createDto);
        // TODO : create By 추가 해야함
        return areaRepository.save(area);
    }
}
