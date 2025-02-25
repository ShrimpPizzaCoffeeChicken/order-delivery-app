package com.fortest.orderdelivery.app.domain.payment.dto;

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
public class PaymentGetListResponseDto {

    private String search;

    @JsonProperty("total-contents")
    private Long totalContents;

    private Integer size;

    @JsonProperty("current-page")
    private Integer currentPage;

    @JsonProperty("order-list")
    private List<PaymentDto> orderList;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentDto {
        @JsonProperty("payment-id")
        private String paymentId;
        @JsonProperty("payment-agent")
        private String paymentAgent;
        @JsonProperty("payment-status")
        private String paymentStatus;
        @JsonProperty("store-name")
        private String storeName;
        private Integer price;
        @JsonProperty("created-at")
        private String createdAt;
        @JsonProperty("updated-at")
        private String updatedAt;
    }
}
