package com.fortest.orderdelivery.app.domain.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderSaveRequestDto {

    @JsonProperty("store-id")
    private String storeId;

    @JsonProperty("order-type")
    private String orderType;

    @JsonProperty("menu-list")
    private List<MenuDto> menuList = new ArrayList<>();

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MenuDto {
        private String id;
        private int count;
        @JsonProperty("option-list")
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
