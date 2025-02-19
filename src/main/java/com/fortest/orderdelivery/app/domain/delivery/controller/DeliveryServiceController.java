package com.fortest.orderdelivery.app.domain.delivery.controller;

import com.fortest.orderdelivery.app.domain.delivery.dto.DeliveryGetDetailResponseDto;
import com.fortest.orderdelivery.app.domain.delivery.dto.DeliveryGetListReponseDto;
import com.fortest.orderdelivery.app.domain.delivery.dto.DeliverySaveRequestDto;
import com.fortest.orderdelivery.app.domain.delivery.dto.DeliverySaveResponseDto;
import com.fortest.orderdelivery.app.domain.delivery.service.DeliveryService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/service")
@RequiredArgsConstructor
@RestController
public class DeliveryServiceController {

    private final DeliveryService deliveryService;

    @PostMapping("/deliveries")
    public ResponseEntity<CommonDto<DeliverySaveResponseDto>> saveDelivery(@RequestBody DeliverySaveRequestDto requestDto) {
        // TODO : 유저 id 획득
        DeliverySaveResponseDto responseDto = deliveryService.saveEntry(requestDto, 123L);

        return ResponseEntity.ok(
                CommonDto.<DeliverySaveResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message("Success")
                        .data(responseDto)
                        .build()
        );
    }

    @GetMapping("/deliveries")
    public ResponseEntity<CommonDto<DeliveryGetListReponseDto>> getDeliveryList(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam("orderby") String orderby,
            @RequestParam("sort") String sort,
            @RequestParam("search") String search
    ) {
        DeliveryGetListReponseDto deliveryList = deliveryService.getDeliveryList(page, size, orderby, sort, search, 123L);

        return ResponseEntity.ok(
                CommonDto.<DeliveryGetListReponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message("Success")
                        .data(deliveryList)
                        .build()
        );
    }

    @GetMapping("/deliveries/{deliveryId}")
    public ResponseEntity<CommonDto<DeliveryGetDetailResponseDto>> getDeliveryDetail (@PathVariable("deliveryId") String deliveryId) {
        DeliveryGetDetailResponseDto deliveryDetail = deliveryService.getDeliveryDetail(deliveryId, 123L);

        return ResponseEntity.ok(
                CommonDto.<DeliveryGetDetailResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message("Success")
                        .data(deliveryDetail)
                        .build()
        );
    }
}
