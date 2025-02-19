package com.fortest.orderdelivery.app.domain.order.dto;

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

    private Long totalContents;

    private Integer size;

    private Integer currentPage;

    private List<OrderDto> orderList;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderDto {
        private String orderId;
        private String storeId;
        private String storeName;
        private Integer price;
        private String createdAt;
        private String updatedAt;
    }
}
