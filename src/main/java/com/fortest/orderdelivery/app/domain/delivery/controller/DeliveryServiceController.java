package com.fortest.orderdelivery.app.domain.delivery.controller;

import com.fortest.orderdelivery.app.domain.delivery.dto.DeliveryGetDetailResponseDto;
import com.fortest.orderdelivery.app.domain.delivery.dto.DeliveryGetListReponseDto;
import com.fortest.orderdelivery.app.domain.delivery.dto.DeliverySaveRequestDto;
import com.fortest.orderdelivery.app.domain.delivery.dto.DeliverySaveResponseDto;
import com.fortest.orderdelivery.app.domain.delivery.service.DeliveryService;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/service")
@RequiredArgsConstructor
@RestController
public class DeliveryServiceController {

    private final MessageUtil messageUtil;
    private final DeliveryService deliveryService;

    @PostMapping("/deliveries")
    public ResponseEntity<CommonDto<DeliverySaveResponseDto>> saveDelivery(@RequestBody DeliverySaveRequestDto requestDto) {
        // TODO : 유저 id 획득
        DeliverySaveResponseDto responseDto = deliveryService.saveEntry(requestDto, new User());

        return ResponseEntity.ok(
                CommonDto.<DeliverySaveResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
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
        // TODO : 유저 id 획득
        DeliveryGetListReponseDto deliveryList = deliveryService.getDeliveryList(page, size, orderby, sort, search, new User());

        return ResponseEntity.ok(
                CommonDto.<DeliveryGetListReponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(deliveryList)
                        .build()
        );
    }

    @GetMapping("/deliveries/{deliveryId}")
    public ResponseEntity<CommonDto<DeliveryGetDetailResponseDto>> getDeliveryDetail (@PathVariable("deliveryId") String deliveryId) {
        // TODO : 유저 id 획득
        DeliveryGetDetailResponseDto deliveryDetail = deliveryService.getDeliveryDetail(deliveryId, new User());
        return ResponseEntity.ok(
                CommonDto.<DeliveryGetDetailResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(deliveryDetail)
                        .build()
        );
    }

    @DeleteMapping("/deliveries/{deliveryId}")
    public ResponseEntity<CommonDto<Map<String, String>>> deleteDelivery (@PathVariable("deliveryId") String deliveryId) {
        // TODO : 유저 id 획득
        String deleteDeliveryId = deliveryService.deleteDelivery(deliveryId, new User());
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
