package com.fortest.orderdelivery.app.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreMenuValidRequestDto {

    private List<MenuDto> menuList = new ArrayList<>();

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MenuDto {
        private String id;
        private List<OptionDto> optionList = new ArrayList<>();

        public void addOption(OptionDto optionDto) {
            this.optionList.add(optionDto);
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OptionDto {
        private String id;
    }

    /**
     * Dto 변환
     * @param orderSaveRequestDto
     * @return StoreMenuValidReqDto
     */
    public static StoreMenuValidRequestDto from (OrderSaveRequestDto orderSaveRequestDto) {
        ArrayList<MenuDto> validMenuList = new ArrayList<>();
        for (OrderSaveRequestDto.MenuDto orderMenuDto : orderSaveRequestDto.getMenuList()) {
            MenuDto validMenu = MenuDto.builder()
                    .id(orderMenuDto.getId())
                    .optionList(new ArrayList<>())
                    .build();
            validMenuList.add(validMenu);
            for (OrderSaveRequestDto.OptionDto orderOptionDto : orderMenuDto.getOptionList()) {
                validMenu.addOption(new OptionDto(orderOptionDto.getId()));
            }
        }

        return StoreMenuValidRequestDto.builder()
                .menuList(validMenuList)
                .build();
    }
}
