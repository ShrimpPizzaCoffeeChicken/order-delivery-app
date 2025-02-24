package com.fortest.orderdelivery.app.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderValidResponseDto {

    @JsonProperty("order-id")
    private String orderId;
    @JsonProperty("order-price")
    private Integer orderPrice;
    @JsonProperty("order-status")
    private String orderStatus;
    @JsonProperty("order-type")
    private String orderType;
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
