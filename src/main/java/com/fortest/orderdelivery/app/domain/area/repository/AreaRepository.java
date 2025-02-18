package com.fortest.orderdelivery.app.domain.area.repository;

import com.fortest.orderdelivery.app.domain.area.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AreaRepository extends JpaRepository<Area, String> {
}
