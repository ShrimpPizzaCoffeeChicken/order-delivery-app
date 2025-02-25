package com.fortest.orderdelivery.app.domain.payment.service;

import com.fortest.orderdelivery.app.domain.payment.dto.PaymentGetListResponseDto;
import com.fortest.orderdelivery.app.domain.payment.dto.PaymentSaveRequestDto;
import com.fortest.orderdelivery.app.domain.payment.dto.PaymentSaveResponseDto;
import com.fortest.orderdelivery.app.domain.payment.entity.Payment;
import com.fortest.orderdelivery.app.domain.payment.entity.PaymentAgent;
import com.fortest.orderdelivery.app.domain.payment.repository.PaymentAgentRepository;
import com.fortest.orderdelivery.app.domain.payment.repository.PaymentRepository;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@ActiveProfiles({"develop"})
@SpringBootTest
class PaymentServiceTest {

    @Autowired
    PaymentAgentRepository paymentAgentRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    PaymentService paymentService;

    @Test
    void saveEntryTest() {

        // given
        String paymentAgentName = "TOSS" + UUID.randomUUID().toString();
        PaymentAgent toss = PaymentAgent.builder()
                .name(paymentAgentName)
                .build();
        paymentAgentRepository.save(toss);

        // when
        PaymentSaveRequestDto requestDto = PaymentSaveRequestDto.builder()
                .orderId("user1")
                .paymentAgent(paymentAgentName)
                .paymentPid("123-123-123")
                .status("COMPLETE")
                .build();

        PaymentSaveResponseDto paymentSaveResponseDto = paymentService.saveEntry(requestDto, new User());

        // then
        log.info("result = {}", paymentSaveResponseDto);
    }

    @Test
    void getPaymentListTest () {

        ArrayList<PaymentAgent> paymentAgents = new ArrayList<>();
        paymentAgents.add(PaymentAgent.builder()
                .name("TOSS")
                .build());
        paymentAgents.add(PaymentAgent.builder()
                .name("KAKAO")
                .build());
        paymentAgentRepository.saveAll(paymentAgents);

        // 결제 생성
        String customerName = "user";
        ArrayList<Payment> payments = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            int num = i % 2;
            payments.add(
                Payment.builder()
                        .orderId(UUID.randomUUID().toString())
                        .customerName(customerName + num)
                        .paymentPid(UUID.randomUUID().toString())
                        .paymentAgent(paymentAgents.get(num))
                        .status(Payment.Status.COMPLETE)
                        .price(i * 1000)
                        .build()
            );
        }
        paymentRepository.saveAll(payments);

        PaymentGetListResponseDto paymentList = paymentService.getPaymentList(
                1,
                1,
                "price",
                "DESC",
                null,
                new User()
        );

        JSONObject jsonObject = new JSONObject(paymentList);
        log.info("result = {}", jsonObject);
    }
}