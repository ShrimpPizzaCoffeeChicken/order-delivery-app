package com.fortest.orderdelivery.app.domain.menu.mapper;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionSaveResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionsSaveRequestDto;
import com.fortest.orderdelivery.app.domain.menu.entity.ExposeStatus;
import com.fortest.orderdelivery.app.domain.menu.entity.Menu;
import com.fortest.orderdelivery.app.domain.menu.entity.MenuOption;

//추후에 Security Filter 생성 후, createBy 등 사용자 정보 넣어주기
public class MenuOptionMapper {

    public static MenuOption toMenuOption(MenuOptionsSaveRequestDto menuOptionsSaveRequestDto, Menu menu) {
        return MenuOption.builder()
            .name(menuOptionsSaveRequestDto.getName())
            .description(menuOptionsSaveRequestDto.getDescription())
            .price(menuOptionsSaveRequestDto.getPrice())
            .exposeStatus(ExposeStatus.valueOf(menuOptionsSaveRequestDto.getExposeStatus()))
            .menu(menu)
            .build();
    }

    public static MenuOptionSaveResponseDto toMenuOptionSaveResponseDto(MenuOption menuOption) {
        return MenuOptionSaveResponseDto.builder()
            .optionId(menuOption.getId())
            .build();
    }
}
