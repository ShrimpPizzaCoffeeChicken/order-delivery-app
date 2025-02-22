package com.fortest.orderdelivery.app.domain.payment.controller;

import com.fortest.orderdelivery.app.domain.payment.dto.*;
import com.fortest.orderdelivery.app.domain.payment.service.PaymentService;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RequiredArgsConstructor
@RequestMapping("/api/service")
@RestController
public class PaymentServiceController {

    private final MessageUtil messageUtil;
    private final PaymentService paymentService;

    @PostMapping("/payments")
    public ResponseEntity<CommonDto<PaymentSaveResponseDto>> savePayment(@RequestBody PaymentSaveRequestDto saveRequestDto) {
        PaymentSaveResponseDto responseDto = paymentService.saveEntry(saveRequestDto, new User());

        return ResponseEntity.ok(
                CommonDto.<PaymentSaveResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(responseDto)
                        .build()
        );
    }

    @GetMapping("/payments")
    public ResponseEntity<CommonDto<PaymentGetListResponseDto>> getPaymentList (
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam("orderby") String orderby,
            @RequestParam("sort") String sort,
            @RequestParam("search") String search
    ) {

        PaymentGetListResponseDto paymentList = paymentService.getPaymentList(page, size, orderby, sort, search, new User());
        return ResponseEntity.ok(
                CommonDto.<PaymentGetListResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(paymentList)
                        .build()
        );
    }

    @DeleteMapping("/payments/{paymentId}")
    public ResponseEntity<CommonDto<Map<String, String>>> deletePayment(@PathVariable("paymentId") String paymentId) {
        String deletePaymentId = paymentService.deletePayment(paymentId, 123L);
        Map<String, String> data = Map.of("payment-id", deletePaymentId);

        return ResponseEntity.ok(
                CommonDto.<Map<String, String>> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(data)
                        .build()
        );
    }

    @PatchMapping("/payments/{paymentId}")
    public ResponseEntity<CommonDto<PaymentUpdateStatusResponseDto>> updateStatus(@PathVariable("paymentId") String paymentId, @RequestBody PaymentUpdateStatusRequestDto requestDto) {
        PaymentUpdateStatusResponseDto responseDto = paymentService.updateStatus(new User(), paymentId, requestDto);

        return ResponseEntity.ok(
                CommonDto.<PaymentUpdateStatusResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(responseDto)
                        .build()
        );
    }
}
