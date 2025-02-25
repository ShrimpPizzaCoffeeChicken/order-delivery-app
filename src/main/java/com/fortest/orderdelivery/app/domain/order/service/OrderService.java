package com.fortest.orderdelivery.app.domain.order.service;

import com.fortest.orderdelivery.app.domain.delivery.dto.DeliveryGetDataResponseDto;
import com.fortest.orderdelivery.app.domain.order.dto.*;
import com.fortest.orderdelivery.app.domain.order.entity.Order;
import com.fortest.orderdelivery.app.domain.order.mapper.OrderMapper;
import com.fortest.orderdelivery.app.domain.order.repository.OrderQueryRepository;
import com.fortest.orderdelivery.app.domain.order.repository.OrderRepository;
import com.fortest.orderdelivery.app.domain.user.entity.RoleType;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.exception.NotFoundException;
import com.fortest.orderdelivery.app.global.exception.NotValidRequestException;
import com.fortest.orderdelivery.app.global.gateway.ApiGateway;
import com.fortest.orderdelivery.app.global.util.JpaUtil;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

    private final ApiGateway apiGateway;
    private final MessageUtil messageUtil;
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    private static final int REMOVE_ABLE_TIME = 5 * 60; // 60초
    private static final String DELIVERY_CANCEL_STATUS = "CANCEL";

    @Transactional
    public OrderGetDetailDataResponseDto getOderDetailData (String orderId) {
        Order order = orderQueryRepository.findOrderDetail(orderId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.order")));
        return OrderMapper.entityToGetDetailDataDto(order);
    }

    @Transactional
    public OrderStatusUpdateResponseDto updateStatus(User user, String orderId, OrderStatusUpdateRequestDto requestDto) {
        // 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.order")));
        Order.OrderStatus beforeStatus = order.getOrderStatus();

        // 주문 상태 변경
        String toStatusString = requestDto.getTo();
        Order.OrderStatus toStatus = Order.getOrderStatusByString(messageUtil, toStatusString);
        order.updateStatus(toStatus);
        order.isUpdatedNow(user.getId());

        // 응답 메세지
        return OrderMapper.entityToStatusUpdateResponseDto(order, beforeStatus);
    }

    /**
     * 내부 호출용 데이터
     * @param orderId
     * @return
     */
    @Transactional
    public OrderGetDataDto getOrderData(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.order")));
        return OrderMapper.entityToGetDataDto(order);
    }

    @Transactional
    public String saveOrder(OrderSaveRequestDto orderSaveRequestDto, User user) {

         String storeId = orderSaveRequestDto.getStoreId();

        // 가게, 메뉴, 옵션 유효성 검사 요청
        StoreMenuValidRequestDto storeMenuValidRequestDto = StoreMenuValidRequestDto.from(orderSaveRequestDto);
        StoreMenuValidResponseDto storeMenuValidDto = apiGateway.getValidStoreMenuFromApp(storeId, storeMenuValidRequestDto); // api 요청

        // 주문 등록
        Order order = OrderMapper
                .saveDtoToEntity(messageUtil, orderSaveRequestDto, storeMenuValidDto, user.getId(), user.getUsername());

        orderRepository.save(order);

        return order.getId();
    }

    /**
     * 검색을 시도한 유저의 주문 목록을 검색
     * @param page
     * @param size
     * @param orderby : 정렬 기준 필드 명
     * @param sort : DESC or ASC
     * @param search : 가게 이름 검색 키워드건 (포함 조건)
     * @param user : 접속한 유저
     * @return
     */
    @Transactional
    public OrderGetListResponseDto getOrderList(Integer page, Integer size, String orderby, String sort, String search, User user) {

        PageRequest pageable = JpaUtil.getNormalPageable(page, size, orderby, sort);
        String username = user.getUsername();
        if (user.getRoleType().getRoleName() == RoleType.RoleName.MANAGER
                || user.getRoleType().getRoleName() == RoleType.RoleName.MASTER) {
            username = null;
        }
        Page<Order> orderPage = orderQueryRepository.findOrderListUsingSearch(pageable, search, username);

        return OrderMapper.pageToGetOrderListDto(orderPage, search);
    }

    @Transactional
    public OrderGetDetailResponseDto getOrderDetail (String orderId, User user) {

        Order order = orderQueryRepository.findOrderDetail(orderId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.order")));

        if (user.getRoleType().getRoleName() == RoleType.RoleName.CUSTOMER
                || user.getRoleType().getRoleName() == RoleType.RoleName.OWNER) {
            if (!order.getCustomerName().equals(user.getUsername())) {
                throw new NotValidRequestException(messageUtil.getMessage("app.order.not-valid-user"));
            }
        }

        return OrderMapper.entityToGetDetailDto(order);
    }

    @Transactional
    public String deleteEntry (String orderId, User user) {
        try {
            return deleteOrder(orderId, user);
        } catch (Exception e) {
            log.error("Fail Cancel Order : orderId = {}", orderId, e);
            if (e instanceof BusinessLogicException) {
                throw e;
            } else {
                throw new BusinessLogicException(messageUtil.getMessage("app.order.cancel-fail"));
            }
        }
    }

    @Transactional
    public String deleteOrder(String orderId, User user) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.order")));

        if (user.getRoleType().getRoleName() == RoleType.RoleName.CUSTOMER
                || user.getRoleType().getRoleName() == RoleType.RoleName.OWNER) {
            throw new NotValidRequestException(messageUtil.getMessage("app.order.not-valid-user"));
        }

        // 결제 가능 시간 검사
        Duration between = Duration.between(order.getCreatedAt(), LocalDateTime.now());
        if (between.getSeconds() > REMOVE_ABLE_TIME) {
            throw new BusinessLogicException(messageUtil.getMessage("app.order.inable-delete"));
        }

        // 배달 정보 확인
        DeliveryGetDataResponseDto deliveryDataFromApp = apiGateway.getDeliveryIdByOrderIdFromApp(order.getId());
        if (deliveryDataFromApp.getIsExist()) {
            apiGateway.updateDeliveryStatusFromApp(deliveryDataFromApp.getDeliveryId(), DELIVERY_CANCEL_STATUS, user);
        }

        order.isDeletedNow(user.getId());
        return order.getId();
    }
}
