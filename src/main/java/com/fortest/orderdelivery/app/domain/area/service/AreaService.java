package com.fortest.orderdelivery.app.domain.area.service;

import com.fortest.orderdelivery.app.domain.area.dto.AreaGetListResponseDto;
import com.fortest.orderdelivery.app.domain.area.dto.AreaSaveRequestDto;
import com.fortest.orderdelivery.app.domain.area.entity.Area;
import com.fortest.orderdelivery.app.domain.area.mapper.AreaMapper;
import com.fortest.orderdelivery.app.domain.area.repository.AreaRepository;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AreaService {

    private final AreaRepository areaRepository;
    private final MessageUtil messageUtil;

    /**
     * 지역 생성
     * @param saveDto
     * @return 생성된 Area Entity
     */
    public Area saveArea(AreaSaveRequestDto saveDto, User user) {
        Area area = AreaMapper.saveDtoToEntity(saveDto);
        area.isCreatedBy(user.getId());
        return areaRepository.save(area);
    }

    public AreaGetListResponseDto getAreaList () {
        List<Area> areas = areaRepository.findAll();
        return AreaMapper.entityListToGetListResponseDto(areas);
    }

    @Transactional
    public String deleteArea(String areaId, User user) {
        Area area = areaRepository.findById(areaId).orElseThrow(()->
                new BusinessLogicException(messageUtil.getMessage("api.call.client-error")));

        area.isDeletedNow(user.getId());
        return area.getId();
    }
}
