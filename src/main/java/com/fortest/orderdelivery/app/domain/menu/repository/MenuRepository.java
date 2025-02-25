package com.fortest.orderdelivery.app.domain.menu.repository;

import com.fortest.orderdelivery.app.domain.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, String> {
    List<Menu> findAllByIdIn(List<String> idList);
}
