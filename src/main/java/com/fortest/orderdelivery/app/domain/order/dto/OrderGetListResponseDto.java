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
public class OrderGetListResponseDto {

    private String search;

    @JsonProperty("total-contents")
    private Long totalContents;

    private Integer size;

    @JsonProperty("current-page")
    private Integer currentPage;

    @JsonProperty("order-list")
    private List<OrderDto> orderList;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderDto {
        @JsonProperty("order-id")
        private String orderId;
        @JsonProperty("store-id")
        private String storeId;
        @JsonProperty("store-name")
        private String storeName;
        private Integer price;
        @JsonProperty("created-at")
        private String createdAt;
        @JsonProperty("updated-at")
        private String updatedAt;
    }
}
