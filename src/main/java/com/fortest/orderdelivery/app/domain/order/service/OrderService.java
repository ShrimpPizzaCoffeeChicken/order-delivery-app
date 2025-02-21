package com.fortest.orderdelivery.app.domain.order.service;

import com.fortest.orderdelivery.app.domain.order.dto.*;
import com.fortest.orderdelivery.app.domain.order.entity.Order;
import com.fortest.orderdelivery.app.domain.order.mapper.OrderMapper;
import com.fortest.orderdelivery.app.domain.order.repository.OrderQueryRepository;
import com.fortest.orderdelivery.app.domain.order.repository.OrderRepository;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.exception.NotFoundException;
import com.fortest.orderdelivery.app.global.exception.NotValidRequestException;
import com.fortest.orderdelivery.app.global.util.JpaUtil;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

    private final WebClient webClient;
    private final MessageUtil messageUtil;
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    private static final int REMOVE_ABLE_TIME = 5 * 60; // 60초
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String USER_APP_URL = "http://{host}:{port}/api/app/user/{userId}";
    private static final String STORE_APP_URL = "http://{host}:{port}/api/app/stores/{storeId}/menus/valid";

    @Transactional
    public OrderStatusUpdateResponseDto updateStatus(Long userId, String orderId, OrderStatusUpdateRequestDto requestDto) {
        // TODO : 유저 검색
        CommonDto<UserResponseDto> validUserResponse = getValidUserFromApp(userId); // api 요청
        if (validUserResponse == null || validUserResponse.getData() == null) {
            throw new BusinessLogicException(messageUtil.getMessage("api.call.server-error"));
        }
        throwByRespCode(validUserResponse.getCode());

        // 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.order")));
        Order.OrderStatus beforeStatus = order.getOrderStatus();
        // 주문 상태 변경
        String toStatusString = requestDto.getTo();
        Order.OrderStatus toStatus = Order.getOrderStatusByString(toStatusString);
        order.updateStatus(toStatus);

        // 응답 메세지
        return OrderMapper.entityToStatusUpdateResponseDto(order, beforeStatus);
    }


    /**
     * 내부 호출용 데이터
     * @param orderId
     * @return
     */
    public OrderGetDataDto getOrderData(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.order")));
        return OrderMapper.entityToGetDataDto(order);
    }

    @Transactional
    public String saveOrder(OrderSaveRequestDto orderSaveRequestDto, Long userId) {

         String storeId = orderSaveRequestDto.getStoreId();

        // TODO : 유저 검색
        CommonDto<UserResponseDto> validUserResponse = getValidUserFromApp(userId); // api 요청
        if (validUserResponse == null || validUserResponse.getData() == null) {
            throw new BusinessLogicException(messageUtil.getMessage("api.call.server-error"));
        }
        throwByRespCode(validUserResponse.getCode());
        String username = validUserResponse.getData().getUsername();

        // TODO : 가게, 메뉴, 옵션 유효성 검사 요청
        StoreMenuValidRequestDto storeMenuValidRequestDto = StoreMenuValidRequestDto.from(orderSaveRequestDto);
        CommonDto<StoreMenuValidResponseDto> storeMenuValidResponse = getValidStoreMenuFromApp(storeId, storeMenuValidRequestDto); // api 요청
        throwByRespCode(storeMenuValidResponse.getCode());
        if (!storeMenuValidResponse.getData().getResult()) {
            throw new BusinessLogicException(
                    messageUtil.getMessage("api.call.client-error")
            );
        }
        StoreMenuValidResponseDto storeMenuValidDto = storeMenuValidResponse.getData();

        // 주문 등록
        Order order = null;
        try {
            order = OrderMapper.saveDtoToEntity(orderSaveRequestDto, storeMenuValidDto, userId, username);
        } catch (IllegalArgumentException e) {
            log.error("Order : Convert Dto to Entity Fail : ", e);
            throw new BusinessLogicException(messageUtil.getMessage("api.call.client-error"));
        }

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
     * @param userId : 접속한 유저 ID
     * @return
     */
    public OrderGetListResponseDto getOrderList(Integer page, Integer size, String orderby, String sort, String search, Long userId) {
        // TODO : 유저 검색
        CommonDto<UserResponseDto> validUserResponse = getValidUserFromApp(userId); // api 요청
        if (validUserResponse == null || validUserResponse.getData() == null) {
            throw new BusinessLogicException(messageUtil.getMessage("api.call.server-error"));
        }
        throwByRespCode(validUserResponse.getCode());
        String username = validUserResponse.getData().getUsername();

        PageRequest pageable = JpaUtil.getNormalPageable(page, size, orderby, sort);
        Page<Order> orderPage;
        if (search == null || search.isBlank() || search.isEmpty()) {
            orderPage = orderQueryRepository.findOrderList(pageable, username);
        } else {
            orderPage = orderQueryRepository.findOrderListUsingSearch(pageable, search, username);
        }
        return OrderMapper.pageToGetOrderListDto(orderPage, search);
    }

    @Transactional
    public OrderGetDetailResponseDto getOrderDetail (String orderId, Long userId) {
        // TODO : 유저 검색
        CommonDto<UserResponseDto> validUserResponse = getValidUserFromApp(userId); // api 요청
        if (validUserResponse == null || validUserResponse.getData() == null) {
            throw new BusinessLogicException(messageUtil.getMessage("api.call.server-error"));
        }
        throwByRespCode(validUserResponse.getCode());
        String username = validUserResponse.getData().getUsername();

        Order order = orderQueryRepository.findOrderDetail(orderId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.order")));
        if (!order.getCustomerName().equals(username)) {
            throw new NotValidRequestException(messageUtil.getMessage("app.order.not-valid-user"));
        }

        return OrderMapper.entityToGetDetailDto(order);
    }

    @Transactional
    public String deleteOrder(String orderId, Long userId) {
        // TODO : 유저 검색
        CommonDto<UserResponseDto> validUserResponse = getValidUserFromApp(userId); // api 요청
        if (validUserResponse == null || validUserResponse.getData() == null) {
            throw new BusinessLogicException(messageUtil.getMessage("api.call.server-error"));
        }
        throwByRespCode(validUserResponse.getCode());
        String username = validUserResponse.getData().getUsername();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.order")));
        if (!order.getCustomerName().equals(username)) {
            throw new NotValidRequestException(messageUtil.getMessage("app.order.not-valid-user"));
        }

        Duration between = Duration.between(order.getCreatedAt(), LocalDateTime.now());
        if (between.getSeconds() > REMOVE_ABLE_TIME) {
            throw new BusinessLogicException(messageUtil.getMessage("app.order.inable-delete"));
        }

        order.isDeletedNow(userId);
        return order.getId();
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

    // TODO : 하단 코드로 교체 예정
    private CommonDto<StoreMenuValidResponseDto> getValidStoreMenuFromApp(String storeId, StoreMenuValidRequestDto validReqDto) {
        ArrayList<StoreMenuValidResponseDto.MenuDto> menuList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            ArrayList<StoreMenuValidResponseDto.OptionDto> optionList = new ArrayList<>();
            for (int j = 0; j < 2; j++) {
                optionList.add(
                        StoreMenuValidResponseDto.OptionDto.builder()
                                .name("optionName" + String.valueOf(i) + String.valueOf(j))
                                .id("option" + String.valueOf(i) + String.valueOf(j))
                                .price(1 + j * 1000)
                                .build()
                );
            }
            menuList.add(
                    StoreMenuValidResponseDto.MenuDto.builder()
                            .name("menuName" + String.valueOf(i))
                            .id("menu" + i)
                            .optionList(optionList)
                            .price(1 + i * 1000)
                            .build()
            );
        }

        StoreMenuValidResponseDto validDto = StoreMenuValidResponseDto.builder()
                .storeId(storeId)
                .storeName("storId123")
                .result(true)
                .menuList(menuList)
                .build();

        return new CommonDto<>("SUCCESS", HttpStatus.OK.value(), validDto);
    }

    /**
     * 가게, 메뉴, 옵션 유효성 검사 요청
     * @param
     * @param
     * @return CommonDto<StoreMenuValidResDto> : 요청 실패 시 null
     */
    //public CommonDto<StoreMenuValidResDto> getValidStoreMenuFromApp(String storeId, StoreMenuValidReqDto validReqDto) {

    //    String targetUrl = STORE_APP_URL
    //            .replace("{host}", "localhost")
    //            .replace("{port}", "8082")
    //            .replace("{storeId}", storeId);

    //    // 요청 파라미터 생성
    //    JSONObject paramObject = new JSONObject(validReqDto);
    //    Map<String, String> queryParam = Map.of(
    //            "data", paramObject.toString()
    //    );

    //    return webClient.get()
    //            .uri(targetUrl, queryParam)
    //            .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    //            .retrieve()
    //            .bodyToMono(new ParameterizedTypeReference<CommonDto<StoreMenuValidResDto>>() {})
    //            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) //에러 발생 시 2초 간격으로 최대 3회 재시도
    //            .onErrorResume(throwable -> {
    //                log.error("Fail : {}, {}", targetUrl, queryParam, throwable);
    //                return Mono.empty();
    //            })
    //            .block();
    //}

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
