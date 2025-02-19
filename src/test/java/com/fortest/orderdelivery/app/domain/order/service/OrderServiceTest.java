package com.fortest.orderdelivery.app.domain.order.service;

import com.fortest.orderdelivery.app.domain.order.dto.OrderGetListResponseDto;
import com.fortest.orderdelivery.app.domain.order.entity.Order;
import com.fortest.orderdelivery.app.domain.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles({"develop"})
@SpringBootTest
class OrderServiceTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderService orderService;

    @BeforeEach
    void initData() {
        // order 생성
        ArrayList<Order> orders = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Order order = Order.builder()
                    .storeId("testId" + i)
                    .storeName("testName" + i)
                    .totalPrice(i * 1000)
                    .orderStatus(i % 2 == 0 ? Order.OrderStatus.WAIT : Order.OrderStatus.FAIL)
                    .orderType(i % 2 == 0 ? Order.OrderType.DELIVERY : Order.OrderType.INSTORE)
                    .customerName("testUser123")
                    .build();
            orders.add(order);
        }
        Order order = Order.builder()
                .storeId("YtestId1")
                .storeName("Ytest1")
                .totalPrice(1234)
                .orderStatus(Order.OrderStatus.WAIT)
                .orderType(Order.OrderType.INSTORE)
                .customerName("YYYUSER123")
                .build();
        orders.add(order);

        orderRepository.saveAll(orders);
    }

    @Test
    @DisplayName("주문 생성 테스트")
    void createService() {
//        String orderId = orderService.saveOrder(null, null);
//        System.out.println("orderId = " + orderId);
    }

    @Test
    @DisplayName("주문 목록 조회 테스트")
    void getOrderListTest() {
        OrderGetListResponseDto result = orderService.getOrderList(
                1,
                2,
                "totalPrice",
                "DESC",
                "testName",
                123L
        );
        JSONObject jsonObject = new JSONObject(result);
        log.info("{}", jsonObject.toString());
    }


}