package com.fortest.orderdelivery.app.domain.menu.service;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuAndOptionValidRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuAndOptionValidRequestDto.MenuList;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuAndOptionValidRequestDto.MenuList.OptionList;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuAndOptionValidResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuAppResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuDto;
import com.fortest.orderdelivery.app.domain.menu.entity.Menu;
import com.fortest.orderdelivery.app.domain.menu.entity.MenuOption;
import com.fortest.orderdelivery.app.domain.menu.mapper.MenuMapper;
import com.fortest.orderdelivery.app.domain.menu.repository.MenuQueryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class MenuAppService {

    private final MenuService menuService;
    private final MenuQueryRepository menuQueryRepository;

    public MenuAppResponseDto getMenuFromApp(List<String> menuId) {
        List<MenuDto> menuList = new ArrayList<>();

        menuId.forEach(id -> {
            Menu menu = menuService.getMenuById(id);
            menuList.add(MenuMapper.toMenuDto(menu));
        });
        return MenuMapper.toMenuAppResponseDto(menuList);
    }


    public MenuAndOptionValidResponseDto validateMenuAndOption(MenuAndOptionValidRequestDto menuAndOptionValidRequestDto) {
        boolean result = true;
        List<MenuList> menuLists = menuAndOptionValidRequestDto.getMenuList();

        //request menuList 없는 경우 함수 return
        if (CollectionUtils.isEmpty(menuLists)) {
            result = false;
            return MenuMapper.toMenuAndOptionValidResponseDto(null, result);
        }

        List<MenuAndOptionValidResponseDto.MenuList> resultMenuList = new ArrayList<>();

        //List에 있는 메뉴들을 하나씩 꺼내서 for문 돌기
        for (MenuList menu : menuLists) {
            String menuId = menu.getId();
            List<OptionList> optionLists = menu.getOptionList();
            Menu savedMenu = menuQueryRepository.getMenuWithMenuOption(menuId);

            List<MenuOption> menuOptionList = savedMenu.getMenuOptionList();
            List<MenuAndOptionValidResponseDto.MenuList.OptionList> resultOptionList = new ArrayList<>();

            if (CollectionUtils.isEmpty(optionLists)) {
                resultMenuList.add(MenuAndOptionValidResponseDto.MenuList.builder()
                    .id(menuId)
                    .price(savedMenu.getPrice())
                    .name(savedMenu.getName())
                    .optionList(null)
                    .build());
                continue;
            }

            for (OptionList option : optionLists) {
                for (MenuOption menuOption : menuOptionList) {
                    if (Objects.equals(option.getId(), menuOption.getId())) {
                        if (!Objects.equals(menuId, menuOption.getMenu().getId())) {
                            return MenuMapper.toMenuAndOptionValidResponseDto(null, false);
                        }
                        resultOptionList.add(MenuAndOptionValidResponseDto.MenuList.OptionList.builder()
                            .id(menuOption.getId())
                            .price(menuOption.getPrice())
                            .name(menuOption.getName())
                            .build());
                        break;
                    }
                }
            }

            if(resultOptionList.size() != optionLists.size()) {
                return MenuMapper.toMenuAndOptionValidResponseDto(null, false);
            }

            resultMenuList.add(MenuAndOptionValidResponseDto.MenuList.builder()
                .id(menuId)
                .price(savedMenu.getPrice())
                .name(savedMenu.getName())
                .optionList(resultOptionList)
                .build());
        }

        if(resultMenuList.size() != menuLists.size()) {
            resultMenuList = null;
            result = false;
        }

        return MenuMapper.toMenuAndOptionValidResponseDto(resultMenuList, result);

    }
}
