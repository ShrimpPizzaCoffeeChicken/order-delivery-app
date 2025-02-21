package com.fortest.orderdelivery.app.domain.order.controller;

import com.fortest.orderdelivery.app.domain.order.dto.*;
import com.fortest.orderdelivery.app.domain.order.service.OrderService;
import com.fortest.orderdelivery.app.domain.user.entity.User;
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
        String orderId = orderService.saveOrder(orderSaveRequestDto, new User());
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
        OrderGetListResponseDto orderList = orderService.getOrderList(page, size, orderby, sort, search, new User());

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
        OrderGetDetailResponseDto orderDetail = orderService.getOrderDetail(orderId, new User());
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
        String deletedOrderId = orderService.deleteOrder(orderId, new User());
        Map<String, String> data = Map.of("order-id", deletedOrderId);
        return ResponseEntity.ok(
                CommonDto.<Map<String, String>> builder()
                        .code(HttpStatus.OK.value())
                        .message("Success")
                        .data(data)
                        .build()
        );
    }

    @PatchMapping("/orders/{orderId}")
    public ResponseEntity<CommonDto<OrderStatusUpdateResponseDto>> updateStatus(@PathVariable("orderId") String orderId, @RequestBody OrderStatusUpdateRequestDto requestDto) {
        OrderStatusUpdateResponseDto responseDto = orderService.updateStatus(new User(), orderId, requestDto);

        return ResponseEntity.ok(
                CommonDto.<OrderStatusUpdateResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message("Success")
                        .data(responseDto)
                        .build()
        );
    }

}
