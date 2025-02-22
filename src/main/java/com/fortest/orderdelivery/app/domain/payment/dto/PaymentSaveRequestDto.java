package com.fortest.orderdelivery.app.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSaveRequestDto {

    @Size(min = 1, max = 50, message = "")
    @JsonProperty("order-id")
    private String orderId;

    @NotBlank(message = "payment-agent 는 1자 이상 100자 이하만 입력 가능합니다.")
    @JsonProperty("payment-agent")
    private String paymentAgent;

    @NotBlank(message = "payment-pid 는 공백일 수 없습니다.")
    @JsonProperty("payment-pid")
    private String paymentPid;

    @NotBlank(message = "status 는 공백일 수 없습니다.")
    private String status;
}
