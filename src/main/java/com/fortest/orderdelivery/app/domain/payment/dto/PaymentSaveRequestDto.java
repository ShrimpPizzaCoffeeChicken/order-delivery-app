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
public class PaymentSaveRequestDto {

    @JsonProperty("order-id")
    private String orderId;

    @JsonProperty("payment-agent")
    private String paymentAgent;

    @JsonProperty("payment-pid")
    private String paymentPid;

    private String status;
}
