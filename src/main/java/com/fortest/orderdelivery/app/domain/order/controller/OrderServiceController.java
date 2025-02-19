package com.fortest.orderdelivery.app.domain.order.controller;

import com.fortest.orderdelivery.app.domain.order.dto.OrderGetDetailResponseDto;
import com.fortest.orderdelivery.app.domain.order.dto.OrderGetListResponseDto;
import com.fortest.orderdelivery.app.domain.order.dto.OrderSaveRequestDto;
import com.fortest.orderdelivery.app.domain.order.service.OrderService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/orders")
    public ResponseEntity<CommonDto<OrderGetListResponseDto>> getOrderList(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam("orderby") String orderby,
            @RequestParam("sort") String sort,
            @RequestParam("search") String search
    ) {
        // TODO : 회원 정보 획득
        OrderGetListResponseDto orderList = orderService.getOrderList(page, size, orderby, sort, search, 123L);

        return ResponseEntity.ok(
                CommonDto.<OrderGetListResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message("Success")
                        .data(orderList)
                        .build()
        );
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<CommonDto<OrderGetDetailResponseDto>> getOrderDetail(@PathVariable("orderId") String orderId) {
        OrderGetDetailResponseDto orderDetail = orderService.getOrderDetail(orderId, 123L);
        return ResponseEntity.ok(
                CommonDto.<OrderGetDetailResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message("Success")
                        .data(orderDetail)
                        .build()
        );
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<CommonDto<Map<String, String>>> deleteOrder (@PathVariable("orderId") String orderId) {
        String deletedOrderId = orderService.deleteOrder(orderId, 123L);
        Map<String, String> data = Map.of("order-id", deletedOrderId);
        return ResponseEntity.ok(
                CommonDto.<Map<String, String>> builder()
                        .code(HttpStatus.OK.value())
                        .message("Success")
                        .data(data)
                        .build()
        );
    }
}
