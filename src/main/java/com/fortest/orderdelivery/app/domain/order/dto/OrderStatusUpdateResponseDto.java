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
public class OrderStatusUpdateResponseDto {
    @JsonProperty("before-status")
    private String beforeStatus;
    @JsonProperty("after-status")
    private String afterStatus;
}
