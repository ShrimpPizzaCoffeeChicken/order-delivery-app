package com.fortest.orderdelivery.app.domain.order.controller;

import com.fortest.orderdelivery.app.domain.order.dto.OrderSaveRequestDto;
import com.fortest.orderdelivery.app.domain.order.service.OrderService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/api/service/orders")
    public CommonDto<?> saveOrder(@RequestBody OrderSaveRequestDto orderSaveRequestDto) {
        // TODO : TEMP : userId 를 UserDetail 에서 획득해야함
        String orderId = orderService.saveOrder(orderSaveRequestDto, 123L);
        Map<String, String> body = Map.of("order-id", orderId);

        return CommonDto.builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(body)
                .build();
    }
}
