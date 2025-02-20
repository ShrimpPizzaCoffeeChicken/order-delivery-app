package com.fortest.orderdelivery.app.domain.menu.service;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionAppResponseDto;
import com.fortest.orderdelivery.app.domain.menu.entity.MenuOption;
import com.fortest.orderdelivery.app.domain.menu.mapper.MenuMapper;
import com.fortest.orderdelivery.app.domain.menu.repository.MenuOptionQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuOptionAppService {

    private final MenuOptionQueryRepository menuOptionQueryRepository;

    public MenuOptionAppResponseDto getMenuOptionFromApp(List<String> menuOptionIdList) {
        List<MenuOption> menuOptionList = menuOptionQueryRepository.getMenuOptionsAndMenusByMenuOptionIds(menuOptionIdList);

        return MenuMapper.toMenuOptionAppResponseDto(menuOptionList);
    }
}
