package com.fortest.orderdelivery.app.domain.delivery.mapper;

import com.fortest.orderdelivery.app.domain.delivery.dto.*;
import com.fortest.orderdelivery.app.domain.delivery.entity.Delivery;
import com.fortest.orderdelivery.app.domain.payment.dto.OrderValidResponseDto;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Optional;

public class DeliveryMapper {

    public static Delivery saveDtoToEntity(DeliverySaveRequestDto saveRequestDto, String username) {
        return Delivery.builder()
                .customerName(username)
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

    public static DeliveryGetListReponseDto entityToGetListDto (Page<Delivery> page, String search) {
        DeliveryGetListReponseDto.DeliveryGetListReponseDtoBuilder builder = DeliveryGetListReponseDto.builder();
        builder = builder
                .search(search == null ? "" : search)
                .totalContents(page.getTotalElements())
                .size(page.getSize())
                .currentPage(page.getNumber() + 1);
        ArrayList<DeliveryGetListReponseDto.DeliveryDto> deliveryDtos = new ArrayList<>();
        for (Delivery delivery : page.getContent()) {
            deliveryDtos.add(
                    DeliveryGetListReponseDto.DeliveryDto.builder()
                            .deliveryId(delivery.getId())
                            .address(delivery.getAddress())
                            .status(delivery.getStatus().name())
                            .build()
            );
        }
        return builder.deliveryList(deliveryDtos).build();
    }

    public static DeliveryGetDetailResponseDto entityToGetDetailDto(Delivery delivery, OrderValidResponseDto orderData) {
        return DeliveryGetDetailResponseDto.builder()
                .address(delivery.getAddress())
                .status(delivery.getStatus().name())
                .orderId(orderData.getOrderId())
                .storeName(orderData.getStoreName())
                .build();
    }

    public static DeliveryGetDataResponseDto entityToGetDataResponseDto ( Delivery delivery ) {
        if (delivery != null) {
            return DeliveryGetDataResponseDto.builder()
                    .isExist(true)
                    .deliveryId(delivery.getId())
                    .build();
        }

        return DeliveryGetDataResponseDto.builder()
                .isExist(false)
                .deliveryId(null)
                .build();
    }
}
