package com.fortest.orderdelivery.app.domain.delivery.controller;

import com.fortest.orderdelivery.app.domain.delivery.dto.*;
import com.fortest.orderdelivery.app.domain.delivery.service.DeliveryService;
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
@RequestMapping("/api/service")
@RequiredArgsConstructor
@RestController
public class DeliveryServiceController {

    private final MessageUtil messageUtil;
    private final DeliveryService deliveryService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/deliveries")
    public ResponseEntity<CommonDto<DeliverySaveResponseDto>> saveDelivery(@Valid @RequestBody DeliverySaveRequestDto requestDto,
                                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        DeliverySaveResponseDto responseDto = deliveryService.saveEntry(requestDto, userDetails.getUser());

        return ResponseEntity.ok(
                CommonDto.<DeliverySaveResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(responseDto)
                        .build()
        );
    }

    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('MASTER')")
    @GetMapping("/deliveries")
    public ResponseEntity<CommonDto<DeliveryGetListReponseDto>> getDeliveryList(
            @Valid DeliveryGetListRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        DeliveryGetListReponseDto deliveryList = deliveryService.getDeliveryList(
                requestDto.getPage(),
                requestDto.getSize(),
                requestDto.getOrderby(),
                requestDto.getSort(),
                requestDto.getSearch(),
                userDetails.getUser()
        );

        return ResponseEntity.ok(
                CommonDto.<DeliveryGetListReponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(deliveryList)
                        .build()
        );
    }

    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('MASTER')")
    @GetMapping("/deliveries/{deliveryId}")
    public ResponseEntity<CommonDto<DeliveryGetDetailResponseDto>> getDeliveryDetail (@PathVariable("deliveryId") String deliveryId,
                                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        DeliveryGetDetailResponseDto deliveryDetail = deliveryService.getDeliveryDetail(deliveryId, userDetails.getUser());
        return ResponseEntity.ok(
                CommonDto.<DeliveryGetDetailResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(deliveryDetail)
                        .build()
        );
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('MASTER')")
    @DeleteMapping("/deliveries/{deliveryId}")
    public ResponseEntity<CommonDto<Map<String, String>>> deleteDelivery (@PathVariable("deliveryId") String deliveryId,
                                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String deleteDeliveryId = deliveryService.deleteDelivery(deliveryId, userDetails.getUser());
        Map<String, String> data = Map.of("delivery-id", deleteDeliveryId);

        return ResponseEntity.ok(
                CommonDto.<Map<String, String>> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(data)
                        .build()
        );
    }
}
