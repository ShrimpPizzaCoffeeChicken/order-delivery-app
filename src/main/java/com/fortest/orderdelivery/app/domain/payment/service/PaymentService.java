package com.fortest.orderdelivery.app.domain.payment.service;

import com.fortest.orderdelivery.app.domain.order.entity.Order;
import com.fortest.orderdelivery.app.domain.payment.dto.*;
import com.fortest.orderdelivery.app.domain.payment.entity.Payment;
import com.fortest.orderdelivery.app.domain.payment.entity.PaymentAgent;
import com.fortest.orderdelivery.app.domain.payment.mapper.PaymentMapper;
import com.fortest.orderdelivery.app.domain.payment.repository.PaymentAgentRepository;
import com.fortest.orderdelivery.app.domain.payment.repository.PaymentQueryRepository;
import com.fortest.orderdelivery.app.domain.payment.repository.PaymentRepository;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.exception.NotFoundException;
import com.fortest.orderdelivery.app.global.gateway.ApiGateway;
import com.fortest.orderdelivery.app.global.util.JpaUtil;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final ApiGateway apiGateway;
    private final MessageUtil messageUtil;
    private final PaymentRepository paymentRepository;
    private final PaymentQueryRepository paymentQueryRepository;
    private final PaymentAgentRepository paymentAgentRepository;

    @Transactional
    public PaymentUpdateStatusResponseDto updateStatus (User user, String paymentId, PaymentUpdateStatusRequestDto requestDto) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.user")));

        Payment.Status beforeStatus = payment.getStatus();
        String toStatusString = requestDto.getTo();
        Payment.Status toStatus = Payment.getStatusByString(messageUtil, toStatusString);

        payment.updateStatus(toStatus);
        payment.isUpdatedNow(user.getId());

        return new PaymentUpdateStatusResponseDto(beforeStatus.name(), toStatus.name());
    }

    @Transactional
    public PaymentSaveResponseDto saveEntry (PaymentSaveRequestDto saveRequestDto, User user) {
        try {
            return savePayment(saveRequestDto, user);
        } catch (Exception e) {
            // TODO : 결제 실패 처리
            // TODO : 주문 실패 상태 업데이트 요청
            log.error("", e);
            throw new BusinessLogicException(messageUtil.getMessage("app.payment.payment-save-fail"));
        }
    }

    @Transactional
    public PaymentSaveResponseDto savePayment(PaymentSaveRequestDto saveRequestDto, User user) {

        // TODO : 주문 정보 요청 : 외부 요청으로 교체 예정
        OrderValidResponseDto validOrder = apiGateway.getValidOrderFromApp(saveRequestDto.getOrderId());
        PaymentAgent paymentAgent = paymentAgentRepository.findByName(saveRequestDto.getPaymentAgent())
                .orElseThrow(() -> new BusinessLogicException(messageUtil.getMessage("api.call.client-error")));

        // 주문 유효성 검사
        if ( ! Order.OrderStatus.WAIT.name().equals(validOrder.getOrderStatus()) ) {
            throw new BusinessLogicException(messageUtil.getMessage("app.payment.invalid-order"));
        }

        Payment payment = PaymentMapper.saveDtoToEntity(messageUtil, saveRequestDto, paymentAgent, validOrder.getCustomerName(), validOrder.getOrderPrice());
        payment.isCreatedBy(user.getId());
        paymentRepository.save(payment);

        // TODO : 주문 상태 업데이트

        return PaymentMapper.entityToSaveResponseDto(payment);
    }

    /**
     * 결제 목록 조회
     * @param page
     * @param size
     * @param orderby : 정렬 기준 필드 이름
     * @param sort : DESC or ASC
     * @param search : 주문 번호 검색 키워드 (일치 조건)
     * @param user : 로그인 유저
     * @return
     */
    @Transactional
    public PaymentGetListResponseDto getPaymentList (Integer page, Integer size, String orderby, String sort, String search, User user) {

        PageRequest pageable = JpaUtil.getNormalPageable(page, size, orderby, sort);
        Page<Payment> paymentPage;

        paymentPage = paymentQueryRepository.findPaymentListUsingSearch(pageable, user.getUsername(), search);
        return PaymentMapper.pageToGetOrderListDto(paymentPage, null);
    }

    @Transactional
    public String deletePayment(String paymentId, User user) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.payment")));
        payment.isDeletedNow(user.getId());
        return payment.getId();
    }
}
