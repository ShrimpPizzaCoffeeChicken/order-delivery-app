package com.fortest.orderdelivery.app.domain.delivery.service;

import com.fortest.orderdelivery.app.domain.delivery.dto.DeliveryGetDetailResponseDto;
import com.fortest.orderdelivery.app.domain.delivery.dto.DeliveryGetListReponseDto;
import com.fortest.orderdelivery.app.domain.delivery.dto.DeliverySaveRequestDto;
import com.fortest.orderdelivery.app.domain.delivery.dto.DeliverySaveResponseDto;
import com.fortest.orderdelivery.app.domain.delivery.entity.Delivery;
import com.fortest.orderdelivery.app.domain.delivery.mapper.DeliveryMapper;
import com.fortest.orderdelivery.app.domain.delivery.repository.DeliveryQueryRepository;
import com.fortest.orderdelivery.app.domain.delivery.repository.DeliveryRepository;
import com.fortest.orderdelivery.app.domain.order.dto.UserResponseDto;
import com.fortest.orderdelivery.app.domain.order.entity.Order;
import com.fortest.orderdelivery.app.domain.payment.dto.OrderValidResponseDto;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.exception.NotFoundException;
import com.fortest.orderdelivery.app.global.exception.NotValidRequestException;
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
public class DeliveryService {

    private final WebClient webClient;
    private final MessageUtil messageUtil;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryQueryRepository deliveryQueryRepository;

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String ORDER_APP_URL = "http://{host}:{port}/api/app/orders/{orderId}";

    public DeliverySaveResponseDto saveEntry(DeliverySaveRequestDto saveRequestDto, Long userId) {
        try {
            return saveDelivery(saveRequestDto, userId);
        } catch (Exception e) {
            // TODO : 배달 등록 실패 처리
            // TODO : 주문 실패 상태 업데이트 요청
            log.error("", e);
            throw new BusinessLogicException(messageUtil.getMessage("app.delivery.delivery-save-fail"));
        }
    }

    @Transactional
    public DeliverySaveResponseDto saveDelivery(DeliverySaveRequestDto saveRequestDto, Long userId) {

        // TODO : 유저 검색
        CommonDto<UserResponseDto> validUserResponse = getValidUserFromApp(userId); // api 요청
        if (validUserResponse == null || validUserResponse.getData() == null) {
            throw new BusinessLogicException(messageUtil.getMessage("api.call.server-error"));
        }
        throwByRespCode(validUserResponse.getCode());
        String username = validUserResponse.getData().getUsername();

        // TODO : 주문 검증 : 추후 외부 요청으로 교체 예정
        CommonDto<OrderValidResponseDto> validOrderFromApp = getValidOrderFromApp(saveRequestDto.getOrderId());
        if (validOrderFromApp == null || validOrderFromApp.getData() == null) {
            throw new BusinessLogicException(messageUtil.getMessage("api.call.server-error"));
        }
        throwByRespCode(validOrderFromApp.getCode());

        // 주문 유효성 검사
        if ( ! Order.OrderStatus.PAYED.name().equals(validOrderFromApp.getData().getOrderStatus()) ) {
            throw new BusinessLogicException(messageUtil.getMessage("app.delivery.invalid-order"));
        }

        Delivery delivery = DeliveryMapper.saveDtoToEntity(saveRequestDto, username);
        deliveryRepository.save(delivery);

        return DeliveryMapper.entityToSaveResponseDto(delivery);
    }

    @Transactional
    public DeliveryGetDetailResponseDto getDeliveryDetail(String deliveryId, Long userId) {
        // TODO : 유저 검색
        CommonDto<UserResponseDto> validUserResponse = getValidUserFromApp(userId); // api 요청
        if (validUserResponse == null || validUserResponse.getData() == null) {
            throw new BusinessLogicException(messageUtil.getMessage("api.call.server-error"));
        }
        throwByRespCode(validUserResponse.getCode());
        String username = validUserResponse.getData().getUsername();

        Delivery delivery = deliveryQueryRepository.findDeliveryDetail(deliveryId, username)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.delivery")));

        // TODO : 주문 검증 : 추후 외부 요청으로 교체 예정
        CommonDto<OrderValidResponseDto> validOrderFromApp = getValidOrderFromApp(delivery.getOrderId());
        if (validOrderFromApp == null || validOrderFromApp.getData() == null) {
            throw new BusinessLogicException(messageUtil.getMessage("api.call.server-error"));
        }
        throwByRespCode(validOrderFromApp.getCode());

        return DeliveryMapper.entityToGetDetailDto(delivery, validOrderFromApp.getData());
    }

    /**
     * 검색을 시도한 유저의 배달 목록을 검색
     * @param page
     * @param size
     * @param orderby : 정렬 기준 필드 명
     * @param sort : DESC or ASC
     * @param search : 배달 상태 문자열 키워드건 (일치 조건)
     * @param userId : 접속한 유저 ID
     * @return
     */
    @Transactional
    public DeliveryGetListReponseDto getDeliveryList (Integer page, Integer size, String orderby, String sort, String search, Long userId) {
        // TODO : 유저 검색
        CommonDto<UserResponseDto> validUserResponse = getValidUserFromApp(userId); // api 요청
        if (validUserResponse == null || validUserResponse.getData() == null) {
            throw new BusinessLogicException(messageUtil.getMessage("api.call.server-error"));
        }
        throwByRespCode(validUserResponse.getCode());
        String username = validUserResponse.getData().getUsername();

        PageRequest pageable = JpaUtil.getNormalPageable(page, size, orderby, sort);
        Page<Delivery> deliveryPage;
        if (search == null || search.isBlank() || search.isEmpty()) {
            deliveryPage = deliveryQueryRepository.findDeliveryList(pageable, username);
        } else {
            deliveryPage = deliveryQueryRepository.findDeliveryListUsingSearch(pageable, search, username);
        }
        return DeliveryMapper.entityToGetListDto(deliveryPage, search);
    }

    @Transactional
    public String deleteDelivery(String deliveryId, Long userId) {
        // TODO : 유저 검색
        CommonDto<UserResponseDto> validUserResponse = getValidUserFromApp(userId); // api 요청
        if (validUserResponse == null || validUserResponse.getData() == null) {
            throw new BusinessLogicException(messageUtil.getMessage("api.call.server-error"));
        }
        throwByRespCode(validUserResponse.getCode());
        String username = validUserResponse.getData().getUsername();

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.delivery")));

        if (! delivery.getCustomerName().equals(username)) {
            throw new NotValidRequestException(messageUtil.getMessage("app.delivery.not-valid-user"));
        }

        delivery.isDeletedNow(userId);
        return delivery.getId();
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
                throw new BusinessLogicException(messageUtil.getMessage("api.call.client-error"));
            }
            case 5 -> {
                throw new BusinessLogicException(messageUtil.getMessage("api.call.server-error"));
            }
        }
    }
}
