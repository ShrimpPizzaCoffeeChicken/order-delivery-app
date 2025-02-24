package com.fortest.orderdelivery.app.global.gateway;

import com.fortest.orderdelivery.app.domain.ai.dto.StoreResponseDto;
import com.fortest.orderdelivery.app.domain.delivery.dto.DeliveryGetDataResponseDto;
import com.fortest.orderdelivery.app.domain.image.dto.ImageResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.*;
import com.fortest.orderdelivery.app.domain.order.dto.OrderStatusUpdateResponseDto;
import com.fortest.orderdelivery.app.domain.order.dto.StoreMenuValidRequestDto;
import com.fortest.orderdelivery.app.domain.order.dto.StoreMenuValidResponseDto;
import com.fortest.orderdelivery.app.domain.payment.dto.OrderValidResponseDto;
import com.fortest.orderdelivery.app.domain.review.dto.OrderDetailsResponseDto;
import com.fortest.orderdelivery.app.domain.store.dto.MenuOptionValidReponseDto;
import com.fortest.orderdelivery.app.domain.store.dto.MenuOptionValidRequestDto;
import com.fortest.orderdelivery.app.domain.user.dto.UserResponseDto;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.ApiCallFailException;
import com.fortest.orderdelivery.app.global.exception.GetDataFailException;
import com.fortest.orderdelivery.app.global.exception.UnacceptableHttpCodeException;
import com.fortest.orderdelivery.app.global.jwt.JwtUtil;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class ApiGateway {

    private final JwtUtil jwtUtil;
    private final WebClient webClient;
    private final MessageUtil messageUtil;

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String USER_APP_URL = "http://{host}:{port}/api/app/users/{userId}";
    private static final String STORE_VALID_APP_URL = "http://{host}:{port}/api/app/stores/{storeId}/menus/valid";
    private static final String STORE_APP_URL = "http://{host}:{port}/api/app/stores/{storeId}";
    private static final String MENU_APP_URL = "http://{host}:{port}/api/app/menus";
    private static final String MENU_OPTION_APP_URL = "http://{host}:{port}/api/app/menus/options";
    private static final String MENU_VALID_APP_URL = "http://{host}:{port}/api/app/menus/options/valid";
    private static final String IMAGE_UPDATE_APP_URL = "http://{host}:{port}/api/app/images/menus";
    private static final String IMAGE_DELETE_APP_URL = "http://{host}:{port}/api/app/images/menus/{menuId}";
    private static final String IMAGE_OPTION_UPDATE_APP_URL = "http://{host}:{port}/api/app/images/options";
    private static final String IMAGE_OPTION_DELETE_APP_URL = "http://{host}:{port}/api/app/images/options/{optionId}";
    private static final String ORDER_APP_URL = "http://{host}:{port}/api/app/orders/{orderId}";
    private static final String ORDER_DETAILS_APP_URL = "http://{host}:{port}/api/app/orders/{orderId}/details";
    private static final String ORDER_UPDATE_STATUS_APP_URL = "http://{host}:{port}/api/service/orders/{orderId}";
    private static final String DELIVERY_GET_ID_APP_URL = "http://{host}:{port}/api/app/deliveries/orders/{orderId}";
    private static final String DELIVERY_UPDATE_STATUS_APP_URL = "http://{host}:{port}/api/app/deliveries/{deliveryId}";

    // to Delivery Service ---------------------------------------------------------------------------------------------

    public DeliveryGetDataResponseDto updateDeliveryStatusFromApp(String deliveryId,
        String toStatus, User user) {

        String targetUrl = DELIVERY_UPDATE_STATUS_APP_URL
            .replace("{host}", "localhost")
            .replace("{port}", "8082")
            .replace("{deliveryId}", deliveryId);

        Map<String, String> body = Map.of("to", toStatus);

        CommonDto<DeliveryGetDataResponseDto> commonResponse = webClient.patch()
            .uri(targetUrl)
            .bodyValue(body)
            .header(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createAccessTokenForApp(user))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<CommonDto<DeliveryGetDataResponseDto>>() {
            })
            //.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) //에러 발생 시 2초 간격으로 최대 3회 재시도
            .onErrorResume(throwable -> {
                log.error("Fail : url = {}", targetUrl, throwable);
                return Mono.empty();
            })
            .block();

        String messageKey = "api.call.delivery.server-error";
        checkCommonResponseData(targetUrl, commonResponse, messageKey, messageKey);

        return commonResponse.getData();
    }

    /**
     * 주문 ID 를 통한 배송 데이터 존재 유무 확인 from : Order Service
     *
     * @param orderId
     * @return
     */
    public DeliveryGetDataResponseDto getDeliveryIdByOrderIdFromApp(String orderId) {

        String targetUrl = DELIVERY_GET_ID_APP_URL
            .replace("{host}", "localhost")
            .replace("{port}", "8082")
            .replace("{orderId}", orderId);

        CommonDto<DeliveryGetDataResponseDto> commonResponse = webClient.get()
            .uri(targetUrl)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<CommonDto<DeliveryGetDataResponseDto>>() {
            })
            //.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) //에러 발생 시 2초 간격으로 최대 3회 재시도
            .onErrorResume(throwable -> {
                log.error("Fail : url = {}", targetUrl, throwable);
                return Mono.empty();
            })
            .block();

        String messageKey = "api.call.delivery.server-error";
        checkCommonResponseData(targetUrl, commonResponse, messageKey, messageKey);

        return commonResponse.getData();
    }

    // END : to Delivery Service ---------------------------------------------------------------------------------------

    // to Order Service ------------------------------------------------------------------------------------------------

    /**
     * 주문 상태 업데이트 요청 from : Payment, Delivery Service
     *
     * @param orderId
     * @param toStatus
     * @param user
     * @return
     */
    public OrderStatusUpdateResponseDto updateOrderStatusFromApp(String orderId, String toStatus,
        User user) {

        String targetUrl = ORDER_UPDATE_STATUS_APP_URL
            .replace("{host}", "localhost")
            .replace("{port}", "8082")
            .replace("{orderId}", orderId);

        Map<String, String> body = Map.of("to", toStatus);

        CommonDto<OrderStatusUpdateResponseDto> commonResponse = webClient.patch()
            .uri(targetUrl)
            .header(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createAccessTokenForApp(user))
            .bodyValue(body)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<CommonDto<OrderStatusUpdateResponseDto>>() {
            })
            //.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) //에러 발생 시 2초 간격으로 최대 3회 재시도
            .onErrorResume(throwable -> {
                log.error("Fail : url = {}", targetUrl, throwable);
                return Mono.empty();
            })
            .block();

        String messageKey = "api.call.order.server-error";
        checkCommonResponseData(targetUrl, commonResponse, messageKey, messageKey);

        return commonResponse.getData();
    }

    /**
     * 주문 서비스에 메뉴리스트, 옵션리스트 요청 from : Review Service
     *
     * @param orderId
     * @return CommonDto<OrderDetailsResponseDto> : 요청 실패 시 null
     */
    public OrderDetailsResponseDto getOrderDetailsFromApp(String orderId) {

        String targetUrl = ORDER_DETAILS_APP_URL
            .replace("{host}", "localhost")
            .replace("{port}", "8082")
            .replace("{orderId}", orderId);

        CommonDto<OrderDetailsResponseDto> commonResponse = webClient.get()
            .uri(targetUrl)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<CommonDto<OrderDetailsResponseDto>>() {
            })
            //.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) //에러 발생 시 2초 간격으로 최대 3회 재시도
            .onErrorResume(throwable -> {
                log.error("Fail : {}", targetUrl, throwable);
                return Mono.empty();
            })
            .block();

        String messageKey = "api.call.order.server-error";
        checkCommonResponseData(targetUrl, commonResponse, messageKey, messageKey);

        return commonResponse.getData();
    }

    /**
     * 유효한 주문인지 확인 from : Delivery, Payment Service
     *
     * @param orderId
     * @return
     */
    public OrderValidResponseDto getValidOrderFromApp(String orderId, User user) {
        String targetUrl = ORDER_APP_URL
            .replace("{host}", "localhost")
            .replace("{port}", "8082")
            .replace("{orderId}", orderId);

        CommonDto<OrderValidResponseDto> commonResponse = webClient.get()
            .uri(targetUrl)
            .header(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createAccessTokenForApp(user))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<CommonDto<OrderValidResponseDto>>() {
            })
            //.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) //에러 발생 시 2초 간격으로 최대 3회 재시도
            .onErrorResume(throwable -> {
                log.error("Fail : {}", targetUrl, throwable);
                return Mono.empty();
            })
            .block();

        String messageKey = "api.call.order.server-error";
        checkCommonResponseData(targetUrl, commonResponse, messageKey, messageKey);

        return commonResponse.getData();
    }

    // END : to Order Service ------------------------------------------------------------------------------------------

    // to Store Service ------------------------------------------------------------------------------------------------

    /**
     * 가게유효성 검사 요청 from : AiRequest, Menu Service
     *
     * @param storeId
     * @return CommonDto<StoreValidResponseDto> : 요청 실패 시 null
     */
    public StoreResponseDto getValidStoreFromApp(String storeId, User user) {

        String targetUrl = STORE_APP_URL
            .replace("{host}", "localhost")
            .replace("{port}", "8082")
            .replace("{storeId}", storeId);

        CommonDto<StoreResponseDto> commonResponse = webClient.get()
            .uri(targetUrl)
            .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createAccessTokenForApp(user))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<CommonDto<StoreResponseDto>>() {
            })
            //.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) //에러 발생 시 2초 간격으로 최대 3회 재시도
            .onErrorResume(throwable -> {
                log.error("Fail : {}", targetUrl, throwable);
                return Mono.empty();
            })
            .block();

        String connectionFailMessageKey = "api.call.store.check.server-error";
        String dataNullMessageKey = "api.call.store.check.not-found";
        checkCommonResponseData(targetUrl, commonResponse, connectionFailMessageKey,
            dataNullMessageKey);

        return commonResponse.getData();
    }

    /**
     * 가게, 메뉴, 옵션 유효성 검사 요청 from : Order Service
     *
     * @param
     * @param
     * @return CommonDto<StoreMenuValidResDto> : 요청 실패 시 null
     */
    public StoreMenuValidResponseDto getValidStoreMenuFromApp(String storeId, StoreMenuValidRequestDto validReqDto) {
        String targetUrl = STORE_VALID_APP_URL
            .replace("{host}", "localhost")
            .replace("{port}", "8082")
            .replace("{storeId}", storeId);

        // 요청 파라미터 생성
        CommonDto<StoreMenuValidResponseDto> commonResponse = webClient.post()
            .uri(targetUrl)
            .bodyValue(validReqDto)
            .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<CommonDto<StoreMenuValidResponseDto>>() {
            })
            //.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) //에러 발생 시 2초 간격으로 최대 3회 재시도
            .onErrorResume(throwable -> {
                log.error("Fail : {}, {}", targetUrl, validReqDto, throwable);
                return Mono.empty();
            })
            .block();

        String messageKey = "api.call.order.store-menu-valid.server-error";
        checkCommonResponseData(targetUrl, commonResponse, messageKey, messageKey);

        return commonResponse.getData();
    }

    // END : to Store Service ------------------------------------------------------------------------------------------

    // to Menu Service -------------------------------------------------------------------------------------------------

    /**
     * 메뉴 옵션 서비스에 메뉴 옵션 Id로 메뉴 옵션 객체 요청 from Image Service
     *
     * @param menuOptionIdList
     * @return CommonDto<MenuAppResponseDto> : 요청 실패 시 null
     */
    public MenuOptionAppResponseDto getMenuOptionFromApp(List<String> menuOptionIdList, User user) {

        String targetUrl = MENU_OPTION_APP_URL
            .replace("{host}", "localhost")
            .replace("{port}", "8082");

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(targetUrl)
            .queryParam("menuOptionId", menuOptionIdList);

        String finalUri = uriBuilder.build().toString();

        CommonDto<MenuOptionAppResponseDto> commonResponse = webClient.get()
            .uri(finalUri)
            .header(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createAccessTokenForApp(user))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<CommonDto<MenuOptionAppResponseDto>>() {
            })
            //.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) // 에러 발생 시 2초 간격으로 최대 3회 재시도
            .onErrorResume(throwable -> {
                log.error("Fail : {}", finalUri, throwable);
                return Mono.empty();
            })
            .block();

        String messageKey = "api.call.image.menuopntion.menu-not-found";
        checkCommonResponseData(targetUrl, commonResponse, messageKey, messageKey);

        return commonResponse.getData();
    }

    /**
     * 메뉴 서비스에 메뉴 Id로 메뉴 객체 요청 from : Image MenuOption Service
     *
     * @param menuIdList
     * @return CommonDto<MenuAppResponseDto> : 요청 실패 시 null
     */
    public MenuAppResponseDto getMenuFromApp(List<String> menuIdList, User user) {

        String targetUrl = MENU_APP_URL
            .replace("{host}", "localhost")
            .replace("{port}", "8082");

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
            .fromHttpUrl(targetUrl)
            .queryParam("menuId", menuIdList);

        String finalUri = uriBuilder.build().toString();

        CommonDto<MenuAppResponseDto> commonResponse = webClient.get()
            .uri(finalUri)
            .header(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createAccessTokenForApp(user))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<CommonDto<MenuAppResponseDto>>() {
            })
            //.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) // 에러 발생 시 2초 간격으로 최대 3회 재시도
            .onErrorResume(throwable -> {
                log.error("Fail : {}", finalUri, throwable);
                return Mono.empty();
            })
            .block();

        String messageKey = "api.call.menu.menu-not-found";
        checkCommonResponseData(targetUrl, commonResponse, messageKey, messageKey);

        return commonResponse.getData();
    }

    /**
     * 가게, 메뉴, 옵션 유효성 검사 요청 from : Store Service
     *
     * @param
     * @param
     * @return CommonDto<StoreMenuValidResDto> : 요청 실패 시 null
     */
    public MenuOptionValidReponseDto getValidMenuOptionFromMenuApp(
        MenuOptionValidRequestDto validReqDto) {

        String targetUrl = MENU_VALID_APP_URL
            .replace("{host}", "localhost")
            .replace("{port}", "8082");

        CommonDto<MenuOptionValidReponseDto> commonResponse = webClient.post()
            .uri(targetUrl)
            .bodyValue(validReqDto)
            .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<CommonDto<MenuOptionValidReponseDto>>() {
            })
            //.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) //에러 발생 시 2초 간격으로 최대 3회 재시도
            .onErrorResume(throwable -> {
                log.error("Fail : {}", targetUrl, throwable);
                return Mono.empty();
            })
            .block();

        String messageKey = "api.call.menu.valid-menu-option.server-error";
        checkCommonResponseData(targetUrl, commonResponse, messageKey, messageKey);

        return commonResponse.getData();
    }

    // END : to Menu Service -------------------------------------------------------------------------------------------

    // to Image Service ------------------------------------------------------------------------------------------------

    /**
     * 이미지에 메뉴 옵션 Id update 요청 from : MenuOption Service
     *
     * @param requestDto
     * @return CommonDto<Void> : 요청 실패 시 null
     */
    public MenuOptionImageMappingResponseDto saveMenuAndMenuOptionIdToImage(
        MenuOptionImageMappingRequestDto requestDto, User user) {

        String targetUrl = IMAGE_OPTION_UPDATE_APP_URL
            .replace("{host}", "localhost")
            .replace("{port}", "8082");

        CommonDto<MenuOptionImageMappingResponseDto> commonResponse = webClient.patch()
            .uri(targetUrl)
            .header(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createAccessTokenForApp(user))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(Mono.justOrEmpty(requestDto), MenuImageMappingRequestDto.class)
            .retrieve()
            .bodyToMono(
                new ParameterizedTypeReference<CommonDto<MenuOptionImageMappingResponseDto>>() {
                })
            //.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) //에러 발생 시 2초 간격으로 최대 3회 재시도
            .onErrorResume(throwable -> {
                log.error("Fail : {}", targetUrl, throwable);
                return Mono.empty();
            })
            .block();

        String messageKey = "api.call.image.menuoption.mapping-fail";
        checkCommonResponseData(targetUrl, commonResponse, messageKey, messageKey);

        return commonResponse.getData();
    }

    /**
     * 이미지 서비스에 메뉴 옵션 Id로 메뉴 옵션 이미지 삭제 요청 from : MenuOption Service
     *
     * @param optionId
     * @return CommonDto<ImageResponseDto> : 요청 실패 시 null
     */
    public ImageResponseDto deleteMenuOptionImageFromApp(String optionId, User user) {

        String targetUrl = IMAGE_OPTION_DELETE_APP_URL
            .replace("{host}", "localhost")
            .replace("{port}", "8082")
            .replace("{optionId}", optionId);

        CommonDto<ImageResponseDto> commonResponse = webClient.delete()
            .uri(targetUrl)
            .header(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createAccessTokenForApp(user))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<CommonDto<ImageResponseDto>>() {
            })
            //.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) // 에러 발생 시 2초 간격으로 최대 3회 재시도
            .onErrorResume(throwable -> {
                log.error("Fail : {}", targetUrl, throwable);
                return Mono.empty();
            })
            .block();

        String messageKey = "api.call.image.menuoption.delete-fail";
        checkCommonResponseData(targetUrl, commonResponse, messageKey, messageKey);

        return commonResponse.getData();
    }

    /**
     * 이미지에 메뉴 Id update 요청 from : Menu Service
     *
     * @param requestDto
     * @return CommonDto<Void> : 요청 실패 시 null
     */
    public MenuImageMappingResponseDto saveMenuIdToImage(
        MenuImageMappingRequestDto requestDto, User user) {

        String targetUrl = IMAGE_UPDATE_APP_URL
            .replace("{host}", "localhost")
            .replace("{port}", "8082");

        CommonDto<MenuImageMappingResponseDto> commonResponse = webClient.patch()
            .uri(targetUrl)
            .header(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createAccessTokenForApp(user))
            .body(Mono.justOrEmpty(requestDto), MenuImageMappingRequestDto.class)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<CommonDto<MenuImageMappingResponseDto>>() {
            })
            //.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) //에러 발생 시 2초 간격으로 최대 3회 재시도
            .onErrorResume(throwable -> {
                log.error("Fail : {}", targetUrl, throwable);
                return Mono.empty();
            })
            .block();

        String messageKey = "api.call.image.menu.mapping-fail";
        checkCommonResponseData(targetUrl, commonResponse, messageKey, messageKey);

        return commonResponse.getData();
    }

    /**
     * 이미지 서비스에 메뉴 옵션 Id로 메뉴 옵션 이미지 삭제 요청 from Menu Service
     *
     * @param menuId
     * @return CommonDto<ImageResponseDto> : 요청 실패 시 null
     */
    public ImageResponseDto deleteMenuImageFromApp(String menuId, User user) {

        String targetUrl = IMAGE_DELETE_APP_URL
            .replace("{host}", "localhost")
            .replace("{port}", "8082")
            .replace("{menuId}", menuId);

        CommonDto<ImageResponseDto> commonResponse = webClient.delete()
            .uri(targetUrl)
            .header(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createAccessTokenForApp(user))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<CommonDto<ImageResponseDto>>() {
            })
            //.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) // 에러 발생 시 2초 간격으로 최대 3회 재시도
            .onErrorResume(throwable -> {
                log.error("Fail : {}", targetUrl, throwable);
                return Mono.empty();
            })
            .block();

        String messageKey = "api.call.image.menu-image.delete-fail";
        checkCommonResponseData(targetUrl, commonResponse, messageKey, messageKey);

        return commonResponse.getData();
    }

    // END : to Image Service ------------------------------------------------------------------------------------------

    // to User Service -------------------------------------------------------------------------------------------------

    /**
     * userId를 기반으로 유저 정보를 조회하는 메서드 from : All Service
     */
    public UserResponseDto getUserByIdFromApp(Long userId, String accessToken) {
        try {
            String targetUrl = USER_APP_URL
                .replace("{host}", "localhost")
                .replace("{port}", "8082")
                .replace("{userId}", userId.toString());

            CommonDto<UserResponseDto> commonResponse = webClient.get()
                .uri(targetUrl)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CommonDto<UserResponseDto>>() {
                })
                .block();

            String messageKey = "app.user.not-found-user-id";
            checkCommonResponseData(targetUrl, commonResponse, messageKey, messageKey);

            return commonResponse.getData();
        } catch (Exception e) {
            log.error("유저 정보 조회 에러", e);
            throw e;
        }
    }

    // END : User Service ----------------------------------------------------------------------------------------------

    /**
     * CommonDto null 확인 및 data null 확인 CommonDto.code 를 확인 : 400, 500 번대 상황 시 예외 발생
     *
     * @param targetUrl
     * @param commonResponse
     * @param connectionFailMessageKey
     * @param dataNullMessageKey
     */
    private void checkCommonResponseData(String targetUrl, CommonDto<?> commonResponse,
        String connectionFailMessageKey, String dataNullMessageKey) {
        if (commonResponse == null) { // 통신 실패
            log.error("CommonResponse is Null : url = {}", targetUrl);
            throw new ApiCallFailException(messageUtil.getMessage(connectionFailMessageKey));
        }

        throwByRespCode(commonResponse.getCode()); // HttpStatus 400, 500 대 검사

        if (commonResponse.getData() == null) { // 서버에 에러 터지는 경우
            log.error("CommonResponse.data is Null : url = {}", targetUrl);
            throw new GetDataFailException(messageUtil.getMessage(dataNullMessageKey));
        }
    }

    private void throwByRespCode(int httpStatusCode) {
        int firstNum = httpStatusCode / 100;
        switch (firstNum) {
            case 4 -> {
                throw new UnacceptableHttpCodeException(
                    messageUtil.getMessage("api.call.client-error"));
            }
            case 5 -> {
                throw new UnacceptableHttpCodeException(
                    messageUtil.getMessage("api.call.server-error"));
            }
        }
    }
}
