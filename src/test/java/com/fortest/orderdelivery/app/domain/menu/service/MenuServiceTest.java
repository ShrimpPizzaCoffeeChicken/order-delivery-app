package com.fortest.orderdelivery.app.domain.menu.service;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuAndOptionValidRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuAndOptionValidRequestDto.MenuList;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuAndOptionValidRequestDto.MenuList.OptionList;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuAndOptionValidResponseDto;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles({"develop"})
@SpringBootTest
public class MenuServiceTest {

    @Autowired
    MenuAppService menuAppService;

    @Test
    @DisplayName("메뉴 옵션 검증 테스트")
    void 메뉴는있고삭제된옵션의경우테스트() {
        //메뉴 + 삭제된 옵션으로 request 날렸을 때 false를 줘야할지 ??

        OptionList option1 = MenuList.OptionList.builder()
            .id("953af5e8-7c41-47af-96a3-c46a491a1618")
            .build();

        OptionList option2 = MenuList.OptionList.builder()
            .id("fd467c98-c648-4001-a49e-598d3fb54cbc")
            .build();

        OptionList option3 = MenuList.OptionList.builder()
            .id("0b5f10ca-89c4-47dc-b5c5-1931f8a2857d")
            .build();

        List<OptionList> optionLists1 = List.of(option1, option2);
        List<OptionList> optionLists2 = List.of(option3);

        MenuList menuList1 = MenuList.builder()
            .id("af4bdca5-e979-4b0c-b7ad-9c52fccd1a05")
            .optionList(optionLists1)
            .build();

        MenuList menuList2 = MenuList.builder()
            .id("dbf805d9-30c0-4d12-aa82-f2536285fdf0")
            .optionList(optionLists2)
            .build();

        List<MenuList> menuLists = List.of(menuList1, menuList2);

        MenuAndOptionValidRequestDto requestDto = MenuAndOptionValidRequestDto.builder()
            .menuList(menuLists)
            .build();

        MenuAndOptionValidResponseDto menuAndOptionValidResponseDto  = menuAppService.validateMenuAndOption(requestDto);

        JSONObject jsonObject = new JSONObject(menuAndOptionValidResponseDto);
        log.info("response : " + jsonObject);
    }

}
