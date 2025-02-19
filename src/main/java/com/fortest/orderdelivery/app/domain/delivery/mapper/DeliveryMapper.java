package com.fortest.orderdelivery.app.domain.delivery.mapper;

import com.fortest.orderdelivery.app.domain.delivery.dto.DeliveryGetListDto;
import com.fortest.orderdelivery.app.domain.delivery.dto.DeliverySaveRequestDto;
import com.fortest.orderdelivery.app.domain.delivery.dto.DeliverySaveResponseDto;
import com.fortest.orderdelivery.app.domain.delivery.entity.Delivery;
import org.springframework.data.domain.Page;

import java.util.ArrayList;

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

    public static DeliveryGetListDto entityToGetListDto (Page<Delivery> page, String search) {
        DeliveryGetListDto.DeliveryGetListDtoBuilder builder = DeliveryGetListDto.builder();
        builder = builder
                .search(search == null ? "" : search)
                .totalContents(page.getTotalElements())
                .size(page.getSize())
                .currentPage(page.getNumber() + 1);
        ArrayList<DeliveryGetListDto.DeliveryDto> deliveryDtos = new ArrayList<>();
        for (Delivery delivery : page.getContent()) {
            deliveryDtos.add(
                    DeliveryGetListDto.DeliveryDto.builder()
                            .deliveryId(delivery.getId())
                            .address(delivery.getAddress())
                            .status(delivery.getStatus().name())
                            .build()
            );
        }
        return builder.deliveryList(deliveryDtos).build();
    }
}
