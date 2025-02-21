package com.fortest.orderdelivery.app.domain.payment.service;

import com.fortest.orderdelivery.app.domain.order.dto.UserResponseDto;
import com.fortest.orderdelivery.app.domain.order.entity.Order;
import com.fortest.orderdelivery.app.domain.payment.dto.*;
import com.fortest.orderdelivery.app.domain.payment.entity.Payment;
import com.fortest.orderdelivery.app.domain.payment.entity.PaymentAgent;
import com.fortest.orderdelivery.app.domain.payment.mapper.PaymentMapper;
import com.fortest.orderdelivery.app.domain.payment.repository.PaymentAgentRepository;
import com.fortest.orderdelivery.app.domain.payment.repository.PaymentQueryRepository;
import com.fortest.orderdelivery.app.domain.payment.repository.PaymentRepository;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.exception.NotFoundException;
import com.fortest.orderdelivery.app.global.util.JpaUtil;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final WebClient webClient;
    private final MessageUtil messageUtil;
    private final PaymentRepository paymentRepository;
    private final PaymentQueryRepository paymentQueryRepository;
    private final PaymentAgentRepository paymentAgentRepository;

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String ORDER_APP_URL = "http://{host}:{port}/api/app/orders/{orderId}";

    @Transactional
    public PaymentUpdateStatusResponseDto updateStatus (Long userId, String paymentId, PaymentUpdateStatusRequestDto requestDto) {
        // TODO : 유저 검색
        CommonDto<UserResponseDto> validUserResponse = getValidUserFromApp(userId); // api 요청
        if (validUserResponse == null || validUserResponse.getData() == null) {
            throw new BusinessLogicException(messageUtil.getMessage("api.call.server-error"));
        }
        throwByRespCode(validUserResponse.getCode());

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.user")));

        Payment.Status beforeStatus = payment.getStatus();
        String toStatusString = requestDto.getTo();
        Payment.Status toStatus = Payment.getStatusByString(toStatusString);

        payment.updateStatus(toStatus);
        payment.isUpdatedNow(userId);

        return new PaymentUpdateStatusResponseDto(beforeStatus.name(), toStatus.name());
    }

    @Transactional
    public PaymentSaveResponseDto saveEntry (PaymentSaveRequestDto saveRequestDto) {
        try {
            return savePayment(saveRequestDto);
        } catch (Exception e) {
            // TODO : 결제 실패 처리
            // TODO : 주문 실패 상태 업데이트 요청
            log.error("", e);
            throw new BusinessLogicException(messageUtil.getMessage("app.payment.payment-save-fail"));
        }
    }

    @Transactional
    public PaymentSaveResponseDto savePayment(PaymentSaveRequestDto saveRequestDto) {

        // TODO : 주문 정보 요청 : 외부 요청으로 교체 예정
        CommonDto<OrderValidResponseDto> validOrderFromApp = getValidOrderFromApp(saveRequestDto.getOrderId());
        if (validOrderFromApp == null || validOrderFromApp.getData() == null) {
            throw new BusinessLogicException(messageUtil.getMessage("api.call.server-error"));
        }
        throwByRespCode(validOrderFromApp.getCode());

        PaymentAgent paymentAgent = paymentAgentRepository.findByName(saveRequestDto.getPaymentAgent())
                .orElseThrow(() -> new BusinessLogicException(messageUtil.getMessage("api.call.client-error")));

        // 주문 유효성 검사
        if ( ! Order.OrderStatus.WAIT.name().equals(validOrderFromApp.getData().getOrderStatus()) ) {
            throw new BusinessLogicException(messageUtil.getMessage("app.payment.invalid-order"));
        }

        OrderValidResponseDto validOrder = validOrderFromApp.getData();
        Payment payment = PaymentMapper.saveDtoToEntity(saveRequestDto, paymentAgent, validOrder.getCustomerName(), validOrder.getOrderPrice());
        paymentRepository.save(payment);

        return PaymentMapper.entityToSaveResponseDto(payment);
    }

    /**
     * 결제 목록 조회
     * @param page
     * @param size
     * @param orderby : 정렬 기준 필드 이름
     * @param sort : DESC or ASC
     * @param search : 주문 번호 검색 키워드 (일치 조건)
     * @param userId : 로그인 유저 ID
     * @return
     */
    @Transactional
    public PaymentGetListResponseDto getPaymentList (Integer page, Integer size, String orderby, String sort, String search, Long userId) {

        // TODO : 유저 검색
        CommonDto<UserResponseDto> validUserResponse = getValidUserFromApp(userId); // api 요청
        if (validUserResponse == null || validUserResponse.getData() == null) {
            throw new BusinessLogicException(messageUtil.getMessage("api.call.server-error"));
        }
        throwByRespCode(validUserResponse.getCode());
        String username = validUserResponse.getData().getUsername();

        PageRequest pageable = JpaUtil.getNormalPageable(page, size, orderby, sort);
        Page<Payment> paymentPage;
        if (search == null || search.isBlank() || search.isEmpty()) {
            paymentPage = paymentQueryRepository.findPaymentList(pageable, username);
        } else {
            paymentPage = paymentQueryRepository.findPaymentListUsingSearch(pageable, username, search);
        }
        return PaymentMapper.pageToGetOrderListDto(paymentPage, null);
    }

    @Transactional
    public String deletePayment(String paymentId, Long userId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.payment")));
        payment.isDeletedNow(userId);
        return payment.getId();
    }

    // TODO : 하단 코드로 교체 예정
    private CommonDto<UserResponseDto> getValidUserFromApp(Long userId) {
        String userName = "user" + userId;

        UserResponseDto userDto = UserResponseDto.builder()
                .username(userName)
                .build();

        return new CommonDto<>("SUCCESS", HttpStatus.OK.value(), userDto);
    }

    // private CommonDto<UserResponseDto> getValidUserFromApp(Long userId) {
    //     String targetUrl = USER_APP_URL
    //             .replace("{host}", "localhost")
    //             .replace("{port}", "8082")
    //             .replace("{userId}", userId);
    //     return webClient.get()
    //             .uri(targetUrl)
    //             .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    //             .retrieve()
    //             .bodyToMono(new ParameterizedTypeReference<CommonDto<UserResponseDto>>() {})
    //             .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) //에러 발생 시 2초 간격으로 최대 3회 재시도
    //             .onErrorResume(throwable -> {
    //                 log.error("Fail : {}", targetUrl, throwable);
    //                 return Mono.empty();
    //             })
    //             .block();
    // }

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
                throw new BusinessLogicException(messageUtil.getMessage("api.call.client-error"));
            }
            case 5 -> {
                throw new BusinessLogicException(messageUtil.getMessage("api.call.server-error"));
            }
        }
    }
}
