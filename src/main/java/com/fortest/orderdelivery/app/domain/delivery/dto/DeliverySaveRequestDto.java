package com.fortest.orderdelivery.app.domain.delivery.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliverySaveRequestDto {

    @JsonProperty("order-id")
    private String orderId;

    private String address;
}
