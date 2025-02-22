package com.fortest.orderdelivery.app.domain.order.controller;

import com.fortest.orderdelivery.app.domain.order.dto.*;
import com.fortest.orderdelivery.app.domain.order.service.OrderService;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.security.UserDetailsImpl;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RequiredArgsConstructor
@Validated
@RequestMapping("/api/service")
@RestController
public class OrderServiceController {

    private final MessageUtil messageUtil;
    private final OrderService orderService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/orders")
    public ResponseEntity<CommonDto<Map<String, String>>> saveOrder(@Valid @RequestBody OrderSaveRequestDto orderSaveRequestDto,
                                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {

        String orderId = orderService.saveOrder(orderSaveRequestDto, userDetails.getUser());
        Map<String, String> data = Map.of("order-id", orderId);

        return ResponseEntity.ok(
                CommonDto.<Map<String, String>> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(data)
                        .build()
        );
    }

    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('MASTER')")
    @GetMapping("/orders")
    public ResponseEntity<CommonDto<OrderGetListResponseDto>> getOrderList(
            @Valid OrderGetListRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        OrderGetListResponseDto orderList = orderService.getOrderList(~
                requestDto.getPage(),
                requestDto.getSize(),
                requestDto.getOrderby(),
                requestDto.getSort(),
                requestDto.getSearch(),
                userDetails.getUser()
        );

        return ResponseEntity.ok(
                CommonDto.<OrderGetListResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(orderList)
                        .build()
        );
    }

    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('MASTER')")
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<CommonDto<OrderGetDetailResponseDto>> getOrderDetail(@PathVariable("orderId") String orderId,
                                                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        OrderGetDetailResponseDto orderDetail = orderService.getOrderDetail(orderId, userDetails.getUser());
        return ResponseEntity.ok(
                CommonDto.<OrderGetDetailResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
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
                        .message(messageUtil.getSuccessMessage())
                        .data(data)
                        .build()
        );
    }

    // customer 권한은 결제에 실패했을때를 위해 필요
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('MASTER')")
    @PatchMapping("/orders/{orderId}")
    public ResponseEntity<CommonDto<OrderStatusUpdateResponseDto>> updateStatus(@PathVariable("orderId") String orderId,
                                                                                @RequestBody OrderStatusUpdateRequestDto requestDto,
                                                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        OrderStatusUpdateResponseDto responseDto = orderService.updateStatus(userDetails.getUser(), orderId, requestDto);

        return ResponseEntity.ok(
                CommonDto.<OrderStatusUpdateResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(responseDto)
                        .build()
        );
    }

}
