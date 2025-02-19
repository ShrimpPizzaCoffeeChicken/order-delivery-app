package com.fortest.orderdelivery.app.domain.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderGetDetailResponseDto {

    @JsonProperty("order-id")
    private String orderId;

    @JsonProperty("created-at")
    private String createdAt;

    @JsonProperty("updated-at")
    private String updatedAt;

    @JsonProperty("store-id")
    private String storeId;

    @JsonProperty("store-name")
    private String storeName;

    @JsonProperty("price")
    private Integer price;

    @JsonProperty("menu-list")
    private List<MenuDto> menuList;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MenuDto {
        @JsonProperty("menu-name")
        private String menuName;
        @JsonProperty("menu-count")
        private Integer menuCount;
        @JsonProperty("order-list")
        private List<OptionDto> orderList;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OptionDto {
        @JsonProperty("order-name")
        private String optionName;
        @JsonProperty("order-count")
        private Integer optionCount;
    }
}
