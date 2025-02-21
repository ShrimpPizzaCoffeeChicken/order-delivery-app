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
public class PaymentUpdateStatusResponseDto {
    @JsonProperty("before-status")
    private String beforeStatus;
    @JsonProperty("after-status")
    private String afterStatus;
}
