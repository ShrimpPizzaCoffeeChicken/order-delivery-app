package com.fortest.orderdelivery.app.domain.payment.service;

import com.fortest.orderdelivery.app.domain.order.entity.Order;
import com.fortest.orderdelivery.app.domain.payment.dto.OrderValidResponseDto;
import com.fortest.orderdelivery.app.domain.payment.dto.PaymentSaveRequestDto;
import com.fortest.orderdelivery.app.domain.payment.dto.PaymentSaveResponseDto;
import com.fortest.orderdelivery.app.domain.payment.entity.Payment;
import com.fortest.orderdelivery.app.domain.payment.mapper.PaymentMapper;
import com.fortest.orderdelivery.app.domain.payment.repository.PaymentAgentRepository;
import com.fortest.orderdelivery.app.domain.payment.repository.PaymentRepository;
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
public class PaymentService {

    private final WebClient webClient;
    private final MessageSource messageSource;
    private final PaymentRepository paymentRepository;
    private final PaymentAgentRepository paymentAgentRepository;

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String ORDER_APP_URL = "http://{host}:{port}/api/app/orders/{orderId}";

    @Transactional
    public PaymentSaveResponseDto saveEntry (PaymentSaveRequestDto saveRequestDto) {
        try {
            return savePayment(saveRequestDto);
        } catch (Exception e) {
            // TODO : 결제 실패 처리
            // TODO : 주문 실패 상태 업데이트 요청
            log.error("", e);
            throw new BusinessLogicException(messageSource.getMessage("app.payment.payment-save-fail", null, Locale.KOREA));
        }
    }

    public PaymentSaveResponseDto savePayment(PaymentSaveRequestDto saveRequestDto) {

        // TODO : 주문 정보 요청 : 외부 요청으로 교체 예정
        CommonDto<OrderValidResponseDto> validOrderFromApp = getValidOrderFromApp(saveRequestDto.getOrderId());
        if (validOrderFromApp == null || validOrderFromApp.getData() == null) {
            throw new BusinessLogicException(messageSource.getMessage("api.call.server-error", null, Locale.KOREA));
        }
        throwByRespCode(validOrderFromApp.getCode());

        paymentAgentRepository.findByName(saveRequestDto.getPaymentAgent())
                .orElseThrow(() -> new BusinessLogicException(messageSource.getMessage("api.call.client-error", null, Locale.KOREA)));

        // 주문 유효성 검사
        if ( ! Order.OrderStatus.WAIT.name().equals(validOrderFromApp.getData().getOrderStatus()) ) {
            throw new BusinessLogicException(messageSource.getMessage("app.payment.invalid-order", null, Locale.KOREA));
        }

        Payment payment = PaymentMapper.saveDtoToEntity(saveRequestDto);
        paymentRepository.save(payment);

        return PaymentMapper.entityToSaveResponseDto(payment);
    }

    // TODO : 하단 외부요청 코드로 교체 예정
    private CommonDto<OrderValidResponseDto> getValidOrderFromApp(String orderId) {
        OrderValidResponseDto data = OrderValidResponseDto.builder()
                .orderId(orderId)
                .orderStatus("WAIT")
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
