package com.fortest.orderdelivery.app.domain.menu.service;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuAndOptionValidRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuAndOptionValidRequestDto.MenuList.OptionList;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuAndOptionValidResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuAndOptionValidResponseDto.MenuList;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuAppResponseDto;
import com.fortest.orderdelivery.app.domain.menu.entity.Menu;
import com.fortest.orderdelivery.app.domain.menu.entity.MenuOption;
import com.fortest.orderdelivery.app.domain.menu.mapper.MenuMapper;
import com.fortest.orderdelivery.app.domain.menu.repository.MenuOptionQueryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuAppService {

    private final MenuService menuService;
    private final MenuOptionQueryRepository menuOptionQueryRepository;

    public MenuAppResponseDto getMenuFromApp(List<String> menuId) {
        List<Menu> menuList = new ArrayList<>();

        menuId.forEach(id -> {
            Menu menu = menuService.getMenuById(id);
            menuList.add(menu);
        });
        return MenuMapper.toMenuAppResponseDto(menuList);
    }

    public MenuAndOptionValidResponseDto validateMenuAndOption(MenuAndOptionValidRequestDto menuAndOptionValidRequestDto) {
        List<MenuAndOptionValidResponseDto.MenuList> resultMenuList = new ArrayList<>();
        Boolean result = true;

        if (Objects.isNull(menuAndOptionValidRequestDto.getMenuList())) {
            return null;
        }

        for (MenuAndOptionValidRequestDto.MenuList menu : menuAndOptionValidRequestDto.getMenuList()) {
            String menuId = menu.getId();
            Menu savedMenu = null;

            List<MenuOption> menuOptionList = menuOptionQueryRepository.getMenuOptionsAndMenusByMenuOptionIds(
                menu.getOptionList().stream()
                    .map(OptionList::getId)
                    .toList()
            );

            List<MenuAndOptionValidResponseDto.MenuList.OptionList> resultOptionList = new ArrayList<>();

            if(Objects.isNull(menuOptionList) || menuOptionList.isEmpty()) {
                savedMenu = menuService.getMenuById(menuId);
                resultOptionList = null;
            } else {
                for (MenuOption option : menuOptionList) {
                    if (!Objects.equals(menuId, option.getMenu().getId())) {
                        result = false;
                        break;
                    }

                    resultOptionList.add(MenuAndOptionValidResponseDto.MenuList.OptionList.builder()
                        .id(option.getId())
                        .price(option.getPrice())
                        .name(option.getName())
                        .build());
                }
            }

            if(result && !Objects.isNull(resultOptionList)) {
                resultMenuList.add(MenuAndOptionValidResponseDto.MenuList.builder()
                    .id(menuId)
                    .price(menuOptionList.get(0).getMenu().getPrice())
                    .name(menuOptionList.get(0).getMenu().getName())
                    .optionList(resultOptionList)
                    .build());
            } else if (result) {
                resultMenuList.add(MenuAndOptionValidResponseDto.MenuList.builder()
                    .id(menuId)
                    .price(savedMenu.getPrice())
                    .name(savedMenu.getName())
                    .optionList(null)
                    .build());
            } else {
                break;
            }
        }

        return MenuMapper.toMenuAndOptionValidResponseDto(resultMenuList, result);

    }
}
