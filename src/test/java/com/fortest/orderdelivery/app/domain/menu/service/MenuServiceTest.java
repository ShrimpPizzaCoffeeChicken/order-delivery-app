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

        OptionList option1 = MenuList.OptionList.builder()
            .id("c2841816-f9f7-462b-a563-835fe6104021")
            .build();

        OptionList option2 = MenuList.OptionList.builder()
            .id("241fbae4-c39d-4b9e-9aa5-2bc1e92c6ee2")
            .build();

        OptionList option3 = MenuList.OptionList.builder()
            .id("97eb6f9a-8e66-4b73-b092-681bc8a08583")
            .build();

        List<OptionList> optionLists1 = List.of(option1, option2);
        List<OptionList> optionLists2 = List.of(option3);

        MenuList menuList1 = MenuList.builder()
            .id("4818be43-4c2b-4df1-b8af-56a58addee46")
            .optionList(optionLists1)
            .build();

        MenuList menuList2 = MenuList.builder()
            .id("1110273b-9a64-42b9-8d56-cb0aeb32aac1")
            .optionList(optionLists2)
            .build();

        List<MenuList> menuLists = List.of(menuList1, menuList2);

        MenuAndOptionValidRequestDto requestDto = MenuAndOptionValidRequestDto.builder()
            .menuList(menuLists)
            .build();

        String data = "json";

        MenuAndOptionValidResponseDto menuAndOptionValidResponseDto  = menuAppService.validateMenuAndOption(requestDto);

        JSONObject jsonObject = new JSONObject(menuAndOptionValidResponseDto);
        log.info("response : " + jsonObject);
    }

}
