package com.fortest.orderdelivery.app.domain.delivery.controller;

import com.fortest.orderdelivery.app.domain.delivery.dto.DeliverySaveRequestDto;
import com.fortest.orderdelivery.app.domain.delivery.dto.DeliverySaveResponseDto;
import com.fortest.orderdelivery.app.domain.delivery.service.DeliveryService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/service")
@RequiredArgsConstructor
@RestController
public class DeliveryServiceController {

    private final DeliveryService deliveryService;

    @PostMapping("deliveries")
    public ResponseEntity<CommonDto<DeliverySaveResponseDto>> saveDelivery(@RequestBody DeliverySaveRequestDto requestDto) {
        DeliverySaveResponseDto responseDto = deliveryService.saveEntry(requestDto);

        return ResponseEntity.ok(
                CommonDto.<DeliverySaveResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message("Success")
                        .data(responseDto)
                        .build()
        );
    }
}
