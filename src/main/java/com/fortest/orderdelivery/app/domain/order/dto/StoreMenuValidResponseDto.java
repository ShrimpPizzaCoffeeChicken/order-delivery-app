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
public class StoreMenuValidResponseDto {

    private String storeId;
    private String storeName;
    private Boolean result;
    private List<MenuDto> menuList = new ArrayList<>();

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MenuDto {
        private String id;
        private String name;
        private Integer price;
        private List<OptionDto> optionList = new ArrayList<>();
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OptionDto {
        private String name;
        private String id;
        private Integer price;
    }
}
