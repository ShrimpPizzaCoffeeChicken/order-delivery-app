package com.fortest.orderdelivery.app.domain.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderGetDataDto {

    @JsonProperty("order-id")
    private String orderId;

    @JsonProperty("order-status")
    private String orderStatus;

    @JsonProperty("customer-name")
    private String customerName;

    @JsonProperty("store-id")
    private String storeId;

    @JsonProperty("store-name")
    private String storeName;

    @JsonProperty("created-at")
    private String createdAt;

    @JsonProperty("updated-at")
    private String updatedAt;
}
