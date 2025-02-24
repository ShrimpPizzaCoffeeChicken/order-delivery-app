package com.fortest.orderdelivery.app.domain.menu.mapper;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionsSaveRequestDto;
import com.fortest.orderdelivery.app.domain.menu.entity.ExposeStatus;
import com.fortest.orderdelivery.app.domain.menu.entity.Menu;
import com.fortest.orderdelivery.app.domain.menu.entity.MenuOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public static MenuOption toMenuOption(MenuOptionDto menuOptionDto) {
        return MenuOption.builder()
            .id(menuOptionDto.getId())
            .name(menuOptionDto.getName())
            .description(menuOptionDto.getDescription())
            .price(menuOptionDto.getPrice())
            .exposeStatus(menuOptionDto.getExposeStatus())
            .build();
    }

    public static MenuOptionResponseDto toMenuOptionSaveResponseDto(MenuOption menuOption) {
        return MenuOptionResponseDto.builder()
            .optionId(menuOption.getId())
            .build();
    }

    public static MenuOptionDto toMenuOptionDto(MenuOption menuoption) {
        return MenuOptionDto.builder()
            .id(menuoption.getId())
            .name(menuoption.getName())
            .description(menuoption.getDescription())
            .price(menuoption.getPrice())
            .exposeStatus(menuoption.getExposeStatus())
            .build();
    }

    public static List<MenuOptionDto> toMenuOptionDtoList(List<MenuOption> menuOptionList) {

        return menuOptionList.stream()
            .map(MenuOptionMapper::toMenuOptionDto)
            .collect(Collectors.toList());
    }
}
