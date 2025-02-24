package com.fortest.orderdelivery.app.domain.delivery.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliverySaveRequestDto {

    @Size(min = 1, max = 50, message = "order-id 는 1자 이상 50자 이하입니다.")
    @JsonProperty("order-id")
    private String orderId;

    @NotBlank(message = "address 항목은 공백일 수 없습니다.")
    private String address;
}
