package com.fortest.orderdelivery.app.domain.order.service;

import com.fortest.orderdelivery.app.domain.order.dto.OrderGetDetailResponseDto;
import com.fortest.orderdelivery.app.domain.order.dto.OrderGetListResponseDto;
import com.fortest.orderdelivery.app.domain.order.entity.MenuOptionMenuOrder;
import com.fortest.orderdelivery.app.domain.order.entity.MenuOrder;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

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
//        // order 생성
//        ArrayList<Order> orders = new ArrayList<>();
//        for (int i = 1; i <= 5; i++) {
//            Order order = Order.builder()
//                    .storeId("testId" + i)
//                    .storeName("testName" + i)
//                    .totalPrice(i * 1000)
//                    .orderStatus(i % 2 == 0 ? Order.OrderStatus.WAIT : Order.OrderStatus.FAIL)
//                    .orderType(i % 2 == 0 ? Order.OrderType.DELIVERY : Order.OrderType.INSTORE)
//                    .customerName("testUser123")
//                    .build();
//            orders.add(order);
//        }
//        Order order = Order.builder()
//                .storeId("YtestId1")
//                .storeName("Ytest1")
//                .totalPrice(1234)
//                .orderStatus(Order.OrderStatus.WAIT)
//                .orderType(Order.OrderType.INSTORE)
//                .customerName("YYYUSER123")
//                .build();
//        orders.add(order);
//
//        orderRepository.saveAll(orders);
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

    @Test
    @DisplayName("order detail 조회 테스트")
    @Transactional
    void getOrderDetailTest () {
        // given
        ArrayList<Order> orders = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            int totalPrice = 0;
            // 주문 생성
            Order order = Order.builder()
                    .storeId(UUID.randomUUID().toString())
                    .storeName("가게이름 " + i)
                    .customerName("user" + i)
                    .orderStatus(Order.OrderStatus.WAIT)
                    .orderType(Order.OrderType.INSTORE)
                    .menuOrderList(new ArrayList<>())
                    .build();
            // 메뉴 생성
            for (int j = 1; j <= 3; j++) {
                int menuPrice = i * 1000;
                MenuOrder menuOrder = MenuOrder.builder()
                        .menuId(UUID.randomUUID().toString())
                        .menuName("햄버거" + j)
                        .count(j)
                        .price(menuPrice)
                        .menuOptionMenuOrderList(new ArrayList<>())
                        .build();
                totalPrice += menuPrice;
                order.addMenuOrder(menuOrder);
                // 옵션 생성
                for (int k = 1; k <= 3; k++) {
                    int menuOptionPrice = k * 10;
                    MenuOptionMenuOrder menuOptionMenuOrder = MenuOptionMenuOrder.builder()
                            .menuOptionId(UUID.randomUUID().toString())
                            .menuOptionName("옵션" + k)
                            .menuOptionCount(k)
                            .menuOptionPrice(menuOptionPrice)
                            .build();
                    totalPrice += menuOptionPrice;
                    menuOrder.addMenuOptionMenuOrder(menuOptionMenuOrder);
                }
            }
            order.updateTotalPrice(totalPrice);
            orders.add(order);
        }
        orderRepository.saveAll(orders);
        Order order = orders.get(0);
        log.info("order = {}", order);
        log.info("order.getDeletedAt = {}", order.getDeletedAt());
        String targetIdOwner = order.getId(); // 소유주 일치 케이스
        // String targetIdNotOwner = orders.get(1).getId(); // 소유주 불일치 케이스
        log.info("targetId = {}", targetIdOwner);

        // when
        OrderGetDetailResponseDto result01 = orderService.getOrderDetail(targetIdOwner, 1L);
        JSONObject jsonObject = new JSONObject(result01);
        log.info("result = {}", jsonObject);
    }

    @Test
    @DisplayName("주문 삭제 테스트 : 테스트를 위해 삭제 가능 시간을 짧게 조정")
    void deleteOrderTest () {
        // given
        Order order = Order.builder()
                .storeId(UUID.randomUUID().toString())
                .storeName("가게이름 1")
                .customerName("user1")
                .orderStatus(Order.OrderStatus.WAIT)
                .orderType(Order.OrderType.INSTORE)
                .menuOrderList(new ArrayList<>())
                .build();
        orderRepository.save(order);

        for (int i = 1; i < 4; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info(i + "초 경과");
        }

        String deletedId = orderService.deleteOrder(order.getId(), 1L);
        Order deletedOrder = orderRepository.findById(deletedId).get();
        log.info("result = {}", deletedOrder.getId());
        log.info("deleted At = {} , deletedBy = {}", deletedOrder.getDeletedAt(), deletedOrder.getDeletedBy());
    }
}