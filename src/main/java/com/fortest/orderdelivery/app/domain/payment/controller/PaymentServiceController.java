package com.fortest.orderdelivery.app.domain.payment.controller;

import com.fortest.orderdelivery.app.domain.payment.dto.PaymentSaveRequestDto;
import com.fortest.orderdelivery.app.domain.payment.dto.PaymentSaveResponseDto;
import com.fortest.orderdelivery.app.domain.payment.service.PaymentService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RequestMapping("/api/service")
@RestController
public class PaymentServiceController {

    private final PaymentService paymentService;

    @PostMapping("/payments")
    public ResponseEntity<CommonDto<PaymentSaveResponseDto>> savePayment(@RequestBody PaymentSaveRequestDto saveRequestDto) {
        PaymentSaveResponseDto responseDto = paymentService.saveEntry(saveRequestDto);

        return ResponseEntity.ok(
                CommonDto.<PaymentSaveResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message("Success")
                        .data(responseDto)
                        .build()
        );
    }

    @GetMapping("/payments")
    public void getPaymentList () {

    }
}
