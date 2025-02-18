package com.fortest.orderdelivery.app.domain.delivery.service;

import com.fortest.orderdelivery.app.domain.delivery.dto.DeliverySaveRequestDto;
import com.fortest.orderdelivery.app.domain.delivery.dto.DeliverySaveResponseDto;
import com.fortest.orderdelivery.app.domain.delivery.entity.Delivery;
import com.fortest.orderdelivery.app.domain.delivery.mapper.DeliveryMapper;
import com.fortest.orderdelivery.app.domain.delivery.repository.DeliveryRepository;
import com.fortest.orderdelivery.app.domain.order.entity.Order;
import com.fortest.orderdelivery.app.domain.payment.dto.OrderValidResponseDto;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@Service
public class DeliveryService {

    private final WebClient webClient;
    private final MessageSource messageSource;
    private final DeliveryRepository deliveryRepository;

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String ORDER_APP_URL = "http://{host}:{port}/api/app/orders/{orderId}";

    public DeliverySaveResponseDto saveEntry(DeliverySaveRequestDto saveRequestDto) {
        try {
            return saveDelivery(saveRequestDto);
        } catch (Exception e) {
            // TODO : 배달 등록 실패 처리
            // TODO : 주문 실패 상태 업데이트 요청
            log.error("", e);
            throw new BusinessLogicException(messageSource.getMessage("app.delivery.delivery-save-fail", null, Locale.KOREA));
        }
    }

    @Transactional
    public DeliverySaveResponseDto saveDelivery(DeliverySaveRequestDto saveRequestDto) {

        // TODO : 주문 검증 : 추후 외부 요청으로 교체 예정
        CommonDto<OrderValidResponseDto> validOrderFromApp = getValidOrderFromApp(saveRequestDto.getOrderId());
        if (validOrderFromApp == null || validOrderFromApp.getData() == null) {
            throw new BusinessLogicException(messageSource.getMessage("api.call.server-error", null, Locale.KOREA));
        }
        throwByRespCode(validOrderFromApp.getCode());

        // 주문 유효성 검사
        if ( ! Order.OrderStatus.PAYED.name().equals(validOrderFromApp.getData().getOrderStatus()) ) {
            throw new BusinessLogicException(messageSource.getMessage("app.delivery.invalid-order", null, Locale.KOREA));
        }

        Delivery delivery = DeliveryMapper.saveDtoToEntity(saveRequestDto);
        deliveryRepository.save(delivery);

        return DeliveryMapper.entityToSaveResponseDto(delivery);
    }

    // TODO : 하단 외부요청 코드로 교체 예정
    private CommonDto<OrderValidResponseDto> getValidOrderFromApp(String orderId) {
        OrderValidResponseDto data = OrderValidResponseDto.builder()
                .orderId(orderId)
                .orderStatus("PAYED")
                .build();

        return CommonDto.<OrderValidResponseDto> builder()
                .code(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(data)
                .build();
    }

//    private CommonDto<OrderValidResponseDto> getValidOrderFromApp(String orderId) {
//        String targetUrl = ORDER_APP_URL
//                .replace("{host}", "localhost")
//                .replace("{port}", "8082")
//                .replace("{orderId}", orderId);
//
//        return webClient.get()
//                .uri(targetUrl)
//                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .retrieve()
//                .bodyToMono(new ParameterizedTypeReference<CommonDto<OrderValidResponseDto>>() {})
//                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) //에러 발생 시 2초 간격으로 최대 3회 재시도
//                .onErrorResume(throwable -> {
//                    log.error("Fail : {}", targetUrl, throwable);
//                    return Mono.empty();
//                })
//                .block();
//    }

    private void throwByRespCode(int httpStatusCode) {
        int firstNum = httpStatusCode / 100;
        switch (firstNum) {
            case 4 -> {
                throw new BusinessLogicException(messageSource.getMessage("api.call.client-error", null, Locale.KOREA));
            }
            case 5 -> {
                throw new BusinessLogicException(messageSource.getMessage("api.call.server-error", null, Locale.KOREA));
            }
        }
    }
}
