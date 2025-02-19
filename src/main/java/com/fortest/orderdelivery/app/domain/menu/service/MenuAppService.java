package com.fortest.orderdelivery.app.domain.menu.service;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuAppResponseDto;
import com.fortest.orderdelivery.app.domain.menu.entity.Menu;
import com.fortest.orderdelivery.app.domain.menu.mapper.MenuMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuAppService {

    private final MenuService menuService;

    public MenuAppResponseDto getMenuFromApp(List<String> menuId) {
        List<Menu> menuList = new ArrayList<>();

        menuId.forEach(id -> {
            Menu menu = menuService.getMenuById(id);
            menuList.add(menu);
        });
        return MenuMapper.toMenuAppResponseDto(menuList);
    }
}
