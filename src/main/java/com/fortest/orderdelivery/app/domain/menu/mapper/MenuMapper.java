package com.fortest.orderdelivery.app.domain.menu.mapper;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuAndOptionValidResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuAppResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuGetQueryDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuGetResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuListGetResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuListGetResponseDto.MenuListDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionAppResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuOptionResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuSaveRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuResponseDto;
import com.fortest.orderdelivery.app.domain.menu.entity.ExposeStatus;
import com.fortest.orderdelivery.app.domain.menu.entity.Menu;
import com.fortest.orderdelivery.app.domain.menu.entity.MenuOption;
import java.util.List;
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

    public static MenuResponseDto toMenuResponseDto(Menu menu) {
        return MenuResponseDto.builder()
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

    public static MenuAppResponseDto toMenuAppResponseDto(List<Menu> menuList) {
        return MenuAppResponseDto.builder()
            .menuList(menuList)
            .build();
    }

    public static MenuOptionAppResponseDto toMenuOptionAppResponseDto(List<MenuOption> menuOptionList) {
        return MenuOptionAppResponseDto.builder()
            .menuOptionList(menuOptionList)
            .build();
    }

    public static MenuOptionResponseDto toMenuOptionResponseDto(MenuOption menuOption) {
        return MenuOptionResponseDto.builder()
            .optionId(menuOption.getId())
            .build();
    }

    public static MenuGetResponseDto toMenuGetResponseDto(MenuGetQueryDto menuGetQueryDto, List<String> imageUrlList, List<MenuGetResponseDto.OptionQueryList> optionList) {
        return MenuGetResponseDto.builder()
            .menuName(menuGetQueryDto.getMenuName())
            .menuDescription(menuGetQueryDto.getMenuDescription())
            .menuPrice(menuGetQueryDto.getMenuPrice())
            .menuImageUrl(imageUrlList)
            .optionList(optionList)
            .build();
    }

    public static MenuAndOptionValidResponseDto toMenuAndOptionValidResponseDto(
        List<MenuAndOptionValidResponseDto.MenuList> menuLists,
        Boolean result
        ) {

        return MenuAndOptionValidResponseDto.builder()
            .result(result)
            .menuList(menuLists)
            .build();
    }
}
