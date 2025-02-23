package com.fortest.orderdelivery.app.domain.delivery.controller;

import com.fortest.orderdelivery.app.domain.delivery.dto.DeliveryGetDataResponseDto;
import com.fortest.orderdelivery.app.domain.delivery.dto.DeliveryStatusUpdateResponseDto;
import com.fortest.orderdelivery.app.domain.delivery.service.DeliveryService;
import com.fortest.orderdelivery.app.domain.payment.dto.PaymentUpdateStatusRequestDto;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.security.UserDetailsImpl;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/app")
@RestController
public class DeliveryAppController {

    private final MessageUtil messageUtil;
    private final DeliveryService deliveryService;

    // 조회
    @GetMapping("/deliveries/orders/{orderId}")
    public ResponseEntity<CommonDto<DeliveryGetDataResponseDto>> getDeliveryByOrderId (@PathVariable("orderId") String orderId ){
        DeliveryGetDataResponseDto responseDto = deliveryService.getDeliveryDataByOrderId(orderId);
        return ResponseEntity.ok(
                CommonDto.<DeliveryGetDataResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(responseDto)
                        .build()
        );
    }

    // 업데이트
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('MASTER')")
    @PatchMapping("/deliveries/{deliveryId}")
    public ResponseEntity<CommonDto<DeliveryStatusUpdateResponseDto>> updateStatus(@PathVariable("deliveryId") String deliveryId,
                                                                                   @RequestBody PaymentUpdateStatusRequestDto requestDto,
                                                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        DeliveryStatusUpdateResponseDto responseDto = deliveryService.updateStatus(deliveryId, requestDto.getTo(), userDetails.getUser());
        return ResponseEntity.ok(
                CommonDto.<DeliveryStatusUpdateResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(responseDto)
                        .build()
        );
    }
}
