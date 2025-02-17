package com.fortest.orderdelivery.app.domain.menu.mapper;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuSaveRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuSaveResponseDto;
import com.fortest.orderdelivery.app.domain.menu.entity.ExposeStatus;
import com.fortest.orderdelivery.app.domain.menu.entity.Menu;

public class MenuMapper {

    public static Menu toMenu(MenuSaveRequestDto menuSaveRequestDto) {
        return Menu.builder()
            .name(menuSaveRequestDto.getName())
            .price(menuSaveRequestDto.getPrice())
            .exposeStatus(ExposeStatus.valueOf(menuSaveRequestDto.getExposeStatus()))
            .description(menuSaveRequestDto.getDescription())
            .storeId(menuSaveRequestDto.getStoreId())
            .build();
    }

    public static MenuSaveResponseDto toMenuSaveResponseDto(Menu menu) {
        return MenuSaveResponseDto.builder()
            .menuId(menu.getId())
            .build();
    }
}
