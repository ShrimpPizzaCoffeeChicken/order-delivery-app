package com.fortest.orderdelivery.app.domain.delivery.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliverySaveResponseDto {

    @JsonProperty("delivery-id")
    private String deliveryId;

    @JsonProperty("delivery-status")
    private String deliveryStatus;
}
