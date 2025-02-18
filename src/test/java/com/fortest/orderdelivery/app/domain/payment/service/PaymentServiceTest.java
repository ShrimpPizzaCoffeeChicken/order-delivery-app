package com.fortest.orderdelivery.app.domain.payment.service;

import com.fortest.orderdelivery.app.domain.payment.dto.PaymentSaveRequestDto;
import com.fortest.orderdelivery.app.domain.payment.dto.PaymentSaveResponseDto;
import com.fortest.orderdelivery.app.domain.payment.entity.PaymentAgent;
import com.fortest.orderdelivery.app.domain.payment.repository.PaymentAgentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles({"develop"})
@SpringBootTest
class PaymentServiceTest {

    @Autowired
    PaymentAgentRepository paymentAgentRepository;

    @Autowired
    PaymentService paymentService;

    @BeforeEach
    void mkData() {
        PaymentAgent toss = PaymentAgent.builder()
                .name("TOSS")
                .build();
        paymentAgentRepository.save(toss);
    }

    @Test
    void saveEntryTest() {
        PaymentSaveRequestDto requestDto = PaymentSaveRequestDto.builder()
                .orderId("testOrderId123")
                .paymentAgent("TOSS")
                .paymentPid("123-123-123")
                .status("COMPLETE")
                .build();

        PaymentSaveResponseDto paymentSaveResponseDto = paymentService.saveEntry(requestDto);
        log.info("result = {}", paymentSaveResponseDto);
    }
}