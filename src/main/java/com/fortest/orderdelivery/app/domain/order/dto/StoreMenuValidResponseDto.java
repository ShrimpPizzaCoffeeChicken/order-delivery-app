package com.fortest.orderdelivery.app.domain.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("store-id")
    private String storeId;
    @JsonProperty("store-name")
    private String storeName;
    private Boolean result;
    @JsonProperty("menu-list")
    private List<MenuDto> menuList = new ArrayList<>();

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MenuDto {
        private String id;
        private String name;
        private Integer price;
        @JsonProperty("option-list")
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
