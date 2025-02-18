package com.fortest.orderdelivery.app.domain.payment.mapper;

import com.fortest.orderdelivery.app.domain.payment.dto.PaymentSaveRequestDto;
import com.fortest.orderdelivery.app.domain.payment.dto.PaymentSaveResponseDto;
import com.fortest.orderdelivery.app.domain.payment.entity.Payment;

public class PaymentMapper {

    public static Payment saveDtoToEntity(PaymentSaveRequestDto saveRequestDto) {
        return Payment.builder()
                .orderId(saveRequestDto.getOrderId())
                .paymentAgentId(saveRequestDto.getPaymentAgent())
                .paymentPid(saveRequestDto.getPaymentPid())
                .status(Payment.getStatusByString(saveRequestDto.getStatus()))
                .build();
    }

    public static PaymentSaveResponseDto entityToSaveResponseDto(Payment payment) {
        return PaymentSaveResponseDto.builder()
                .paymentId(payment.getId())
                .status(payment.getStatus().name())
                .build();
    }
}
