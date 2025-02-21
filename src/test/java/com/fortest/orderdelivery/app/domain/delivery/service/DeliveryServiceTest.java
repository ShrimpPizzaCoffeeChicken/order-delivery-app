package com.fortest.orderdelivery.app.domain.delivery.service;

import com.fortest.orderdelivery.app.domain.delivery.dto.DeliverySaveRequestDto;
import com.fortest.orderdelivery.app.domain.delivery.dto.DeliverySaveResponseDto;
import com.fortest.orderdelivery.app.domain.payment.dto.PaymentSaveRequestDto;
import com.fortest.orderdelivery.app.domain.payment.dto.PaymentSaveResponseDto;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles({"develop"})
@SpringBootTest
class DeliveryServiceTest {

    @Autowired
    DeliveryService deliveryService;

    @Test
    void saveEntryTest() {
        DeliverySaveRequestDto requestDto = DeliverySaveRequestDto.builder()
                .orderId("testOrderId123")
                .address("서울시 청와대")
                .build();

        DeliverySaveResponseDto deliverySaveResponseDto = deliveryService.saveDelivery(requestDto, new User());
        log.info("result = {}", deliverySaveResponseDto);
    }
}
