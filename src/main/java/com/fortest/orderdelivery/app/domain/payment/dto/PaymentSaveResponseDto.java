package com.fortest.orderdelivery.app.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSaveResponseDto {

    @JsonProperty("payment-id")
    private String paymentId;

    private String status;
}
