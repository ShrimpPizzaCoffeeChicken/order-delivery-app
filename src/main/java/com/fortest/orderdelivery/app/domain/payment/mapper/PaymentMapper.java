package com.fortest.orderdelivery.app.domain.payment.mapper;

import com.fortest.orderdelivery.app.domain.payment.dto.PaymentGetListResponseDto;
import com.fortest.orderdelivery.app.domain.payment.dto.PaymentSaveRequestDto;
import com.fortest.orderdelivery.app.domain.payment.dto.PaymentSaveResponseDto;
import com.fortest.orderdelivery.app.domain.payment.entity.Payment;
import com.fortest.orderdelivery.app.domain.payment.entity.PaymentAgent;
import com.fortest.orderdelivery.app.global.util.CommonUtil;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class PaymentMapper {

    public static Payment saveDtoToEntity(MessageUtil messageUtil, PaymentSaveRequestDto saveRequestDto, PaymentAgent paymentAgent, String customerName, Integer price) {
        return Payment.builder()
                .orderId(saveRequestDto.getOrderId())
                .customerName(customerName)
                .paymentAgent(paymentAgent)
                .paymentPid(saveRequestDto.getPaymentPid())
                .price(price)
                .status(Payment.getStatusByString(messageUtil, saveRequestDto.getStatus()))
                .build();
    }

    public static PaymentSaveResponseDto entityToSaveResponseDto(Payment payment) {
        return PaymentSaveResponseDto.builder()
                .paymentId(payment.getId())
                .status(payment.getStatus().name())
                .build();
    }

    public static PaymentGetListResponseDto pageToGetOrderListDto(Page<Payment> page, String search) {
        PaymentGetListResponseDto.PaymentGetListResponseDtoBuilder builder = PaymentGetListResponseDto.builder();
        builder = builder
                .search(search == null ? "" : search)
                .totalContents(page.getTotalElements())
                .size(page.getSize())
                .currentPage(page.getNumber() + 1);
        List<PaymentGetListResponseDto.PaymentDto> paymentDtoList = page.getContent().stream()
                .map(PaymentMapper::entityToPaymentListDtoElement)
                .collect(Collectors.toList());
        builder = builder.orderList(paymentDtoList);
        return builder.build();
    }

    public static PaymentGetListResponseDto.PaymentDto entityToPaymentListDtoElement(Payment payment) {
        return PaymentGetListResponseDto.PaymentDto.builder()
                .paymentId(payment.getId())
                .paymentAgent(payment.getPaymentAgent().getName())
                .paymentStatus(payment.getStatus().name())
                .price(payment.getPrice())
                .createdAt(CommonUtil.LDTToString(payment.getCreatedAt()))
                .updatedAt(CommonUtil.LDTToString(payment.getUpdatedAt()))
                .build();
    }
}
