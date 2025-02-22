package com.fortest.orderdelivery.app.domain.order.controller;

import com.fortest.orderdelivery.app.domain.order.dto.OrderGetDataDto;
import com.fortest.orderdelivery.app.domain.order.dto.OrderGetDetailDataResponseDto;
import com.fortest.orderdelivery.app.domain.order.service.OrderService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/app")
@RestController
public class OrderAppController {

    private final MessageUtil messageUtil;
    private final OrderService orderService;

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<CommonDto<OrderGetDataDto>> getOrderData(@PathVariable("orderId") String orderId) {

        OrderGetDataDto orderData = orderService.getOrderData(orderId);

        return ResponseEntity.ok(
                CommonDto.<OrderGetDataDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(orderData)
                        .build()
        );
    }

    @GetMapping("/orders/{orderId}/details")
    public ResponseEntity<CommonDto<OrderGetDetailDataResponseDto>> getOrderDetailData(@PathVariable("orderId") String orderId) {
        OrderGetDetailDataResponseDto responseDto = orderService.getOderDetailData(orderId);
        return ResponseEntity.ok(
                CommonDto.<OrderGetDetailDataResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(responseDto)
                        .build()
        );
    }
}
