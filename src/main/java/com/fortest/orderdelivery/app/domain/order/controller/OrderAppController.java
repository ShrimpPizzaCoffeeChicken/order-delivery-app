package com.fortest.orderdelivery.app.domain.order.controller;

import com.fortest.orderdelivery.app.domain.order.dto.OrderGetDataDto;
import com.fortest.orderdelivery.app.domain.order.dto.OrderGetDetailDataResponseDto;
import com.fortest.orderdelivery.app.domain.order.service.OrderService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Validated
@RequestMapping("/api/app")
@RestController
public class OrderAppController {

    private final MessageUtil messageUtil;
    private final OrderService orderService;

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<CommonDto<OrderGetDataDto>> getOrderData(
            @Valid @Size(min = 1, max = 50) @PathVariable("orderId") String orderId) {

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
    public ResponseEntity<CommonDto<OrderGetDetailDataResponseDto>> getOrderDetailData(
            @Valid @Size(min = 1, max = 50) @PathVariable("orderId") String orderId) {
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
