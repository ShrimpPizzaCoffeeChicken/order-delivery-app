package com.fortest.orderdelivery.app.domain.menu.service;

import com.amazonaws.util.CollectionUtils;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuAndOptionValidRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuAndOptionValidResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuAppResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuDto;
import com.fortest.orderdelivery.app.domain.menu.entity.Menu;
import com.fortest.orderdelivery.app.domain.menu.entity.MenuOption;
import com.fortest.orderdelivery.app.domain.menu.mapper.MenuMapper;
import com.fortest.orderdelivery.app.domain.menu.repository.MenuOptionQueryRepository;
import com.fortest.orderdelivery.app.global.util.CommonUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuAppService {

    private final MenuService menuService;
    private final MenuOptionQueryRepository menuOptionQueryRepository;

    public MenuAppResponseDto getMenuFromApp(List<String> menuId) {
        List<MenuDto> menuList = new ArrayList<>();

        menuId.forEach(id -> {
            Menu menu = menuService.getMenuById(id);
            menuList.add(MenuMapper.toMenuDto(menu));
        });
        return MenuMapper.toMenuAppResponseDto(menuList);
    }


    public MenuAndOptionValidResponseDto validateMenuAndOption(String data) {

        MenuAndOptionValidRequestDto menuAndOptionValidRequestDto = CommonUtil.convertJsonToDto(data, MenuAndOptionValidRequestDto.class);
        boolean result = true;

        //request menuList 없는 경우 함수 return
        if (CollectionUtils.isNullOrEmpty(menuAndOptionValidRequestDto.getMenuList())) {
            result = false;
            return MenuMapper.toMenuAndOptionValidResponseDto(null, result);
        }

        List<MenuAndOptionValidResponseDto.MenuList> resultMenuList = new ArrayList<>();

        //List에 있는 메뉴들을 하나씩 꺼내서 for문 돌기
        for (MenuAndOptionValidRequestDto.MenuList menu : menuAndOptionValidRequestDto.getMenuList()) {
            String menuId = menu.getId();
            Menu savedMenu = menuService.getMenuById(menuId);

            List<MenuOption> menuOptionList = savedMenu.getMenuOptionList();
            List<MenuAndOptionValidResponseDto.MenuList.OptionList> resultOptionList = new ArrayList<>();

            if (CollectionUtils.isNullOrEmpty(menuOptionList)) {
                resultMenuList.add(MenuAndOptionValidResponseDto.MenuList.builder()
                    .id(menuId)
                    .price(savedMenu.getPrice())
                    .name(savedMenu.getName())
                    .optionList(null)
                    .build());
                break;
            }

            for (MenuOption option : menuOptionList) {
                if (!Objects.equals(menuId, option.getMenu().getId())) {
                    result = false; // 실패 처리
                    break;
                }

                //일치하다면 response 용 optionList 생성
                resultOptionList.add(MenuAndOptionValidResponseDto.MenuList.OptionList.builder()
                    .id(option.getId())
                    .price(option.getPrice())
                    .name(option.getName())
                    .build());
            }

            if(!result) {
                return MenuMapper.toMenuAndOptionValidResponseDto(null, result);
            }
        }

        return MenuMapper.toMenuAndOptionValidResponseDto(resultMenuList, result);

    }
}
