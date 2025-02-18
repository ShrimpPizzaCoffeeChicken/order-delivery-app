package com.fortest.orderdelivery.app.domain.delivery.mapper;

import com.fortest.orderdelivery.app.domain.delivery.dto.DeliverySaveRequestDto;
import com.fortest.orderdelivery.app.domain.delivery.dto.DeliverySaveResponseDto;
import com.fortest.orderdelivery.app.domain.delivery.entity.Delivery;

public class DeliveryMapper {

    public static Delivery saveDtoToEntity(DeliverySaveRequestDto saveRequestDto) {
        return Delivery.builder()
                .orderId(saveRequestDto.getOrderId())
                .address(saveRequestDto.getAddress())
                .status(Delivery.Status.END) // 미구현으로 인한 END 등록
                .build();
    }

    public static DeliverySaveResponseDto entityToSaveResponseDto(Delivery delivery) {
        return DeliverySaveResponseDto.builder()
                .deliveryId(delivery.getId())
                .deliveryStatus(delivery.getStatus().name())
                .build();
    }
}
