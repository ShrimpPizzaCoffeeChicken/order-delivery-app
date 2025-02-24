package com.fortest.orderdelivery.app.domain.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@ToString // 로깅을 위해 추가
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreMenuValidRequestDto {

    @JsonProperty("menu-list")
    private List<MenuDto> menuList = new ArrayList<>();

    @ToString // 로깅을 위해 추가
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MenuDto {
        private String id;
        @JsonProperty("option-list")
        private List<OptionDto> optionList = new ArrayList<>();

        public void addOption(OptionDto optionDto) {
            this.optionList.add(optionDto);
        }
    }

    @ToString // 로깅을 위해 추가
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
