package com.fortest.orderdelivery.app.domain.menu.repository;

import com.fortest.orderdelivery.app.domain.menu.entity.MenuOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuOptionRepository extends JpaRepository<MenuOption, String> {

}
