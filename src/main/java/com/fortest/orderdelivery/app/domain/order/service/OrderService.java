package com.fortest.orderdelivery.app.domain.order.service;

import com.fortest.orderdelivery.app.domain.order.dto.OrderSaveRequestDto;
import com.fortest.orderdelivery.app.domain.order.dto.StoreMenuValidRequestDto;
import com.fortest.orderdelivery.app.domain.order.dto.StoreMenuValidResponseDto;
import com.fortest.orderdelivery.app.domain.order.dto.UserResponseDto;
import com.fortest.orderdelivery.app.domain.order.entity.Order;
import com.fortest.orderdelivery.app.domain.order.mapper.OrderMapper;
import com.fortest.orderdelivery.app.domain.order.repository.OrderRepository;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

    private final WebClient webClient;
    private final MessageSource messageSource;
    private final OrderRepository orderRepository;

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String USER_APP_URL = "http://{host}:{port}/api/app/user/{userId}";
    private static final String STORE_APP_URL = "http://{host}:{port}/api/app/store/{storeId}/valid";

    @Transactional
    public String saveOrder(OrderSaveRequestDto orderSaveRequestDto, Long userId) {

         String storeId = orderSaveRequestDto.getStoreId();

        // TODO : 유저 검색
        CommonDto<UserResponseDto> validUserResponse = getValidUserFromApp(userId); // api 요청
        throwByRespCode(validUserResponse.getCode());
        String username = validUserResponse.getData().getUsername();

        // TODO : 가게, 메뉴, 옵션 유효성 검사 요청
        StoreMenuValidRequestDto storeMenuValidRequestDto = StoreMenuValidRequestDto.from(orderSaveRequestDto);
        CommonDto<StoreMenuValidResponseDto> storeMenuValidResponse = getValidStoreMenuFromApp(storeId, storeMenuValidRequestDto); // api 요청
        throwByRespCode(storeMenuValidResponse.getCode());
        if (!storeMenuValidResponse.getData().getResult()) {
            throw new BusinessLogicException(
                    messageSource.getMessage("api.call.client-error", null, Locale.KOREA)
            );
        }
        StoreMenuValidResponseDto storeMenuValidDto = storeMenuValidResponse.getData();

        // 주문 등록
        Order order = null;
        try {
            order = OrderMapper.saveDtoToEntity(orderSaveRequestDto, storeMenuValidDto, userId, username);
        } catch (IllegalArgumentException e) {
            log.error("Order : Convert Dto to Entity Fail : ", e);
            throw new BusinessLogicException(messageSource.getMessage("api.call.client-error", null, Locale.KOREA));
        }

        orderRepository.save(order);

        return order.getId();
    }

    // TODO : 하단 코드로 교체 예정
    private CommonDto<UserResponseDto> getValidUserFromApp(Long userId) {
        String userName = "testUser";

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
                throw new BusinessLogicException(messageSource.getMessage("api.call.client-error", null, Locale.KOREA));
            }
            case 5 -> {
                throw new BusinessLogicException(messageSource.getMessage("api.call.server-error", null, Locale.KOREA));
            }
        }
    }
}
