package com.fortest.orderdelivery.app.domain.order.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderSaveRequestDto {

    private String storeId;

    private String orderType;

    private List<MenuDto> menuList = new ArrayList<>();

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MenuDto {
        private String id;
        private int count;
        private List<OptionDto> optionList = new ArrayList<>();
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OptionDto {
        private String id;
        private int count;
    }
}
