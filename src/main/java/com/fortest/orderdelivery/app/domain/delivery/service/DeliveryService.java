package com.fortest.orderdelivery.app.domain.delivery.service;

import com.fortest.orderdelivery.app.domain.delivery.dto.DeliveryGetDetailResponseDto;
import com.fortest.orderdelivery.app.domain.delivery.dto.DeliveryGetListReponseDto;
import com.fortest.orderdelivery.app.domain.delivery.dto.DeliverySaveRequestDto;
import com.fortest.orderdelivery.app.domain.delivery.dto.DeliverySaveResponseDto;
import com.fortest.orderdelivery.app.domain.delivery.entity.Delivery;
import com.fortest.orderdelivery.app.domain.delivery.mapper.DeliveryMapper;
import com.fortest.orderdelivery.app.domain.delivery.repository.DeliveryQueryRepository;
import com.fortest.orderdelivery.app.domain.delivery.repository.DeliveryRepository;
import com.fortest.orderdelivery.app.domain.order.entity.Order;
import com.fortest.orderdelivery.app.domain.payment.dto.OrderValidResponseDto;
import com.fortest.orderdelivery.app.domain.user.entity.User;
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


@Slf4j
@RequiredArgsConstructor
@Service
public class DeliveryService {

    private final ApiGateway apiGateway;
    private final MessageUtil messageUtil;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryQueryRepository deliveryQueryRepository;

    public DeliverySaveResponseDto saveEntry(DeliverySaveRequestDto saveRequestDto, User user) {
        try {
            return saveDelivery(saveRequestDto, user);
        } catch (Exception e) {
            // TODO : 배달 등록 실패 처리
            // TODO : 주문 상태 업데이트 요청 : 배달 등록 실패
            // TODO : 결제 상태 업데이트 요청 : 취소요청
            log.error("", e);
            throw new BusinessLogicException(messageUtil.getMessage("app.delivery.delivery-save-fail"));
        }
    }

    @Transactional
    public DeliverySaveResponseDto saveDelivery(DeliverySaveRequestDto saveRequestDto, User user) {

        OrderValidResponseDto orderValidDto = apiGateway.getValidOrderFromApp(saveRequestDto.getOrderId());

        // 주문 유효성 검사
        if ( ! Order.OrderStatus.PAYED.name().equals(orderValidDto.getOrderStatus()) ) {
            throw new BusinessLogicException(messageUtil.getMessage("app.delivery.invalid-order"));
        }

        Delivery delivery = DeliveryMapper.saveDtoToEntity(saveRequestDto, user.getUsername());
        deliveryRepository.save(delivery);

        return DeliveryMapper.entityToSaveResponseDto(delivery);
    }

    @Transactional
    public DeliveryGetDetailResponseDto getDeliveryDetail(String deliveryId, User user) {

        Delivery delivery = deliveryQueryRepository.findDeliveryDetail(deliveryId, user.getUsername())
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.delivery")));

        OrderValidResponseDto orderValidDto = apiGateway.getValidOrderFromApp(delivery.getOrderId());

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

        if (! delivery.getCustomerName().equals(user)) {
            throw new NotValidRequestException(messageUtil.getMessage("app.delivery.not-valid-user"));
        }

        delivery.isDeletedNow(user.getId());
        return delivery.getId();
    }
}
