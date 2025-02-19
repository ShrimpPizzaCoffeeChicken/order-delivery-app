package com.fortest.orderdelivery.app.domain.menu.service;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionAppResponseDto;
import com.fortest.orderdelivery.app.domain.menu.entity.Menu;
import com.fortest.orderdelivery.app.domain.menu.entity.MenuOption;
import com.fortest.orderdelivery.app.domain.menu.mapper.MenuMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuOptionAppService {

    private MenuOptionService menuOptionService;

    public MenuOptionAppResponseDto getMenuOptionFromApp(List<String> menuOptionIdList) {

        List<MenuOption> menuOptionList = new ArrayList<>();

        menuOptionIdList.forEach(id -> {
            MenuOption menuOption = menuOptionService.getMenuOptionById(id);
            menuOptionList.add(menuOption);
        });
        return MenuMapper.toMenuOptionAppResponseDto(menuOptionList);
    }
}
