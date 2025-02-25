package com.fortest.orderdelivery.app.domain.payment.controller;

import com.fortest.orderdelivery.app.domain.payment.dto.*;
import com.fortest.orderdelivery.app.domain.payment.service.PaymentService;
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

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/service")
@RestController
public class PaymentServiceController {

    private final MessageUtil messageUtil;
    private final PaymentService paymentService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/payments")
    public ResponseEntity<CommonDto<PaymentSaveResponseDto>> savePayment(@Valid @RequestBody PaymentSaveRequestDto saveRequestDto,
                                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        PaymentSaveResponseDto responseDto = paymentService.saveEntry(saveRequestDto, userDetails.getUser());

        return ResponseEntity.ok(
                CommonDto.<PaymentSaveResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(responseDto)
                        .build()
        );
    }

    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('MASTER')")
    @GetMapping("/payments")
    public ResponseEntity<CommonDto<PaymentGetListResponseDto>> getPaymentList (
            @Valid PaymentGetListRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        PaymentGetListResponseDto paymentList = paymentService.getPaymentList(
                requestDto.getPage(),
                requestDto.getSize(),
                requestDto.getOrderby(),
                requestDto.getSort(),
                requestDto.getSearch(),
                userDetails.getUser()
        );
        return ResponseEntity.ok(
                CommonDto.<PaymentGetListResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(paymentList)
                        .build()
        );
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('MASTER')")
    @DeleteMapping("/payments/{paymentId}")
    public ResponseEntity<CommonDto<Map<String, String>>> deletePayment(@PathVariable("paymentId") String paymentId,
                                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String deletePaymentId = paymentService.deletePayment(paymentId, userDetails.getUser());
        Map<String, String> data = Map.of("payment-id", deletePaymentId);

        return ResponseEntity.ok(
                CommonDto.<Map<String, String>> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(data)
                        .build()
        );
    }

    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('MASTER')")
    @PatchMapping("/payments/{paymentId}")
    public ResponseEntity<CommonDto<PaymentUpdateStatusResponseDto>> updateStatus(@PathVariable("paymentId") String paymentId,
                                                                                  @Valid @RequestBody PaymentUpdateStatusRequestDto requestDto,
                                                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        PaymentUpdateStatusResponseDto responseDto = paymentService.updateStatus(userDetails.getUser(), paymentId, requestDto);

        return ResponseEntity.ok(
                CommonDto.<PaymentUpdateStatusResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(responseDto)
                        .build()
        );
    }
}
