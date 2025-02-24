package com.fortest.orderdelivery.app.domain.delivery.service;

import com.fortest.orderdelivery.app.domain.delivery.dto.*;
import com.fortest.orderdelivery.app.domain.delivery.entity.Delivery;
import com.fortest.orderdelivery.app.domain.delivery.mapper.DeliveryMapper;
import com.fortest.orderdelivery.app.domain.delivery.repository.DeliveryQueryRepository;
import com.fortest.orderdelivery.app.domain.delivery.repository.DeliveryRepository;
import com.fortest.orderdelivery.app.domain.order.entity.Order;
import com.fortest.orderdelivery.app.domain.payment.dto.OrderValidResponseDto;
import com.fortest.orderdelivery.app.domain.user.entity.RoleType;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.global.exception.AlreadyExistException;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.exception.NotFoundException;
import com.fortest.orderdelivery.app.global.exception.NotValidRequestException;
import com.fortest.orderdelivery.app.global.gateway.ApiGateway;
import com.fortest.orderdelivery.app.global.util.JpaUtil;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Service
public class DeliveryService {

    private final ApiGateway apiGateway;
    private final MessageUtil messageUtil;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryQueryRepository deliveryQueryRepository;

    private static final String ORDER_COMPLETE_STATUS = "COMPLETE";
    private static final String ORDER_DELIVERY_FAIL_STATUS = "DELIVERY_FAIL";

    // 주문 아이디로 배달 정보 조회
    public DeliveryGetDataResponseDto getDeliveryDataByOrderId (String orderId) {
        Optional<Delivery> deliveryOptional = deliveryRepository.findByOrderId(orderId);
        return DeliveryMapper.entityToGetDataResponseDto(deliveryOptional.get());
    }

    // 배달 상태 업데이트
    @Transactional
    public DeliveryStatusUpdateResponseDto updateStatus (String deliveryId, String toStatusString, User user) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.delivery")));
        if (user.getRoleType().getRoleName() == RoleType.RoleName.CUSTOMER
                || user.getRoleType().getRoleName() == RoleType.RoleName.OWNER) {
            if (!delivery.getCustomerName().equals(user.getUsername())) {
                throw new NotValidRequestException(messageUtil.getMessage("app.delivery.not-valid-user"));
            }
        }
        Delivery.Status beforeStatus = delivery.getStatus();
        Delivery.Status toStatus = Delivery.Status.getByString(messageUtil, toStatusString);
        delivery.updateStatus(toStatus);
        delivery.isUpdatedNow(user.getId());

        return new DeliveryStatusUpdateResponseDto(beforeStatus.name(), toStatus.name());
    }

    public DeliverySaveResponseDto saveEntry(DeliverySaveRequestDto saveRequestDto, User user) {
        try {
            return saveDelivery(saveRequestDto, user);
        } catch (Exception e) {
            if ( e instanceof AlreadyExistException ) {
                throw e;
            }
            log.error("Fail Save Delivery : {}", saveRequestDto, e);
            apiGateway.updateOrderStatusFromApp(saveRequestDto.getOrderId(), ORDER_DELIVERY_FAIL_STATUS, user);
            if (e instanceof BusinessLogicException) {
                throw e;
            } else {
                throw new BusinessLogicException(messageUtil.getMessage("app.payment.payment-save-fail"));
            }
        }
    }

    @Transactional
    public DeliverySaveResponseDto saveDelivery(DeliverySaveRequestDto saveRequestDto, User user) {

        Optional<Delivery> deliveryOptional = deliveryRepository.findByOrderId(saveRequestDto.getOrderId());
        if (deliveryOptional.isPresent()){
            throw new AlreadyExistException(messageUtil.getMessage("app.delivery.already-exist"));
        };

        OrderValidResponseDto orderValidDto = apiGateway.getValidOrderFromApp(saveRequestDto.getOrderId(), user);

        // 주문 유효성 검사
        if ( ! Order.OrderStatus.PAYED.name().equals(orderValidDto.getOrderStatus())
                || !Order.OrderType.DELIVERY.name().equals(orderValidDto.getOrderType())) {
            throw new BusinessLogicException(messageUtil.getMessage("app.delivery.invalid-order"));
        }

        Delivery delivery = DeliveryMapper.saveDtoToEntity(saveRequestDto, user.getUsername());
        deliveryRepository.save(delivery);

        apiGateway.updateOrderStatusFromApp(delivery.getOrderId(), ORDER_COMPLETE_STATUS, user);

        return DeliveryMapper.entityToSaveResponseDto(delivery);
    }

    @Transactional
    public DeliveryGetDetailResponseDto getDeliveryDetail(String deliveryId, User user) {

        Delivery delivery = deliveryQueryRepository.findDeliveryDetail(deliveryId, user.getUsername())
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.delivery")));

        OrderValidResponseDto orderValidDto = apiGateway.getValidOrderFromApp(delivery.getOrderId(), user);

        return DeliveryMapper.entityToGetDetailDto(delivery, orderValidDto);
    }

    /**
     * 검색을 시도한 유저의 배달 목록을 검색
     * @param page
     * @param size
     * @param orderby : 정렬 기준 필드 명
     * @param sort : DESC or ASC
     * @param search : 배달 상태 문자열 키워드건 (일치 조건)
     * @param user : 접속한 유저
     * @return
     */
    @Transactional
    public DeliveryGetListReponseDto getDeliveryList (Integer page, Integer size, String orderby, String sort, String search, User user) {
        PageRequest pageable = JpaUtil.getNormalPageable(page, size, orderby, sort);
        Page<Delivery> deliveryPage;
        if (search == null || search.isBlank() || search.isEmpty()) {
            deliveryPage = deliveryQueryRepository.findDeliveryList(pageable, user.getUsername());
        } else {
            deliveryPage = deliveryQueryRepository.findDeliveryListUsingSearch(pageable, search, user.getUsername());
        }
        return DeliveryMapper.entityToGetListDto(deliveryPage, search);
    }

    @Transactional
    public String deleteDelivery(String deliveryId, User user) {

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.delivery")));

        if (! delivery.getCustomerName().equals(user.getUsername())) {
            throw new NotValidRequestException(messageUtil.getMessage("app.delivery.not-valid-user"));
        }

        delivery.isDeletedNow(user.getId());
        return delivery.getId();
    }
}
