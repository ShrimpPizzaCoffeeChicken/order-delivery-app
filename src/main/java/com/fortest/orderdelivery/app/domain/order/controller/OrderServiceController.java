package com.fortest.orderdelivery.app.domain.order.controller;

import com.fortest.orderdelivery.app.domain.order.dto.OrderSaveRequestDto;
import com.fortest.orderdelivery.app.domain.order.service.OrderService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RequiredArgsConstructor
@Validated
@RequestMapping("/api/service")
@RestController
public class OrderServiceController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<CommonDto<Map<String, String>>> saveOrder(@RequestBody OrderSaveRequestDto orderSaveRequestDto) {
        // TODO : TEMP : userId 를 UserDetail 에서 획득해야함
        String orderId = orderService.saveOrder(orderSaveRequestDto, 123L);
        Map<String, String> data = Map.of("order-id", orderId);

        return ResponseEntity.ok(
                CommonDto.<Map<String, String>> builder()
                        .code(HttpStatus.OK.value())
                        .message("Success")
                        .data(data)
                        .build()
        );
    }
}
