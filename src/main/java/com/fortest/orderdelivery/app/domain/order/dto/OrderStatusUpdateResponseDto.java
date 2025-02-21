package com.fortest.orderdelivery.app.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusUpdateResponseDto {
    private String beforeStatus;
    private String afterStatus;
}
