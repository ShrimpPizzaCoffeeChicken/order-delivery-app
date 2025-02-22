package com.fortest.orderdelivery.app.domain.delivery.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryGetDataResponseDto {
    @JsonProperty("is-exist")
    private Boolean isExist;
    @JsonProperty("delivery-id")
    private String deliveryId;
}
