package com.fortest.orderdelivery.app.domain.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryStatusUpdateRequestDto {
    private String to;
}
