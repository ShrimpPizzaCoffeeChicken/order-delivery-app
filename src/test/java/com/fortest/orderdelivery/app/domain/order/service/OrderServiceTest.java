package com.fortest.orderdelivery.app.domain.order.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles({"develop"})
@SpringBootTest
class OrderServiceTest {

    @Autowired
    OrderService orderService;

    @Test
    void createService() {
        String orderId = orderService.saveOrder(null, null);
        System.out.println("orderId = " + orderId);
    }


}