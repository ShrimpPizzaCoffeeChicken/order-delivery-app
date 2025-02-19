package com.fortest.orderdelivery.app.domain.menu.mapper;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuListGetResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuListGetResponseDto.MenuListDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuSaveRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuSaveResponseDto;
import com.fortest.orderdelivery.app.domain.menu.entity.ExposeStatus;
import com.fortest.orderdelivery.app.domain.menu.entity.Menu;
import com.fortest.orderdelivery.app.global.util.CommonUtil;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;

//추후에 Security Filter 생성 후, createBy 등 사용자 정보 넣어주기
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

    public static MenuListGetResponseDto toMenuListGetResponseDto(Page<MenuListDto> menuPage) {
        return MenuListGetResponseDto.builder()
            .totalContents(menuPage.getTotalElements())
            .size(menuPage.getSize())
            .currentPage(menuPage.getNumber() + 1)
            .menuList(menuPage.getContent())
            .build();
    }
}
