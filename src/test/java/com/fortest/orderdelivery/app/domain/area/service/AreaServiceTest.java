package com.fortest.orderdelivery.app.domain.area.service;

import com.fortest.orderdelivery.app.domain.area.entity.Area;
import com.fortest.orderdelivery.app.domain.area.repository.AreaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles({"develop"})
@SpringBootTest
class AreaServiceTest {

    @Autowired
    AreaRepository areaRepository;

    @Autowired
    AreaService areaService;

    String areaId;

    @BeforeEach
    void before() {
        // 지역 생성
        Area area = Area.builder()
                .city("서울시")
                .district("구로구")
                .street("개발로")
                .build();
        areaRepository.save(area);
        areaId = area.getId();
        log.info("area {}", area);
        log.info("getDeletedBy {}", area.getDeletedBy());
    }

    @Test
    void deleteTest() {
        String deleteTargetId = areaService.deleteArea(areaId, 123L);
        Area area = areaRepository.findById(deleteTargetId).get();
        log.info("delete result = {}", area);
        log.info("getDeletedBy {}", area.getDeletedBy());
    }

}