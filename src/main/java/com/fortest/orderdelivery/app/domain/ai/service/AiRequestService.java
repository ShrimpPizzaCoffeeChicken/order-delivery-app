package com.fortest.orderdelivery.app.domain.ai.service;

import com.fortest.orderdelivery.app.domain.ai.dto.AiRequestGetListResponseDto;
import com.fortest.orderdelivery.app.domain.ai.dto.AiRequestSaveRequestDto;
import com.fortest.orderdelivery.app.domain.ai.dto.AiRequestSaveResponseDto;
import com.fortest.orderdelivery.app.domain.ai.dto.StoreResponseDto;
import com.fortest.orderdelivery.app.domain.ai.entity.AiRequest;
import com.fortest.orderdelivery.app.domain.ai.mapper.AiRequestMapper;
import com.fortest.orderdelivery.app.domain.ai.repository.AiRequestQueryRepository;
import com.fortest.orderdelivery.app.domain.ai.repository.AiRequestRepository;
import com.fortest.orderdelivery.app.domain.order.dto.UserResponseDto;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.exception.NotValidRequestException;
import com.fortest.orderdelivery.app.global.util.JpaUtil;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Service
public class AiRequestService {

    private final MessageUtil messageUtil;
    private final WebClient webClient;
    private final AiRequestRepository aiRequestRepository;
    private final AiRequestQueryRepository aiRequestQueryRepository;

    @Value("${app.api.ai.key}")
    private String apiKey;
    @Value("${app.api.ai.url}")
    private String apiUrl;
    @Value("${app.api.ai.question-base}")
    private String questionBase;

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String STORE_APP_URL = "http://{host}:{port}/api/app/stores/{storeId}";

    /**
     * 검색을 시도한 유저의 주문 목록을 검색
     * @param storeId : 조회할 가게 ID
     * @param page
     * @param size
     * @param orderby : 정렬 기준 필드 명
     * @param sort : DESC or ASC
     * @param search : 질문에 포함된 키워드 (포함 조건)
     * @param userId : 접속한 유저 ID
     * @return
     */
    @Transactional
    public AiRequestGetListResponseDto getAiRequestList(String storeId, Integer page, Integer size, String orderby, String sort, String search, Long userId) {
        // TODO : 유저 검색
        CommonDto<UserResponseDto> validUserResponse = getValidUserFromApp(userId); // api 요청
        if (validUserResponse == null || validUserResponse.getData() == null) {
            throw new BusinessLogicException(messageUtil.getMessage("api.call.server-error"));
        }
        throwByRespCode(validUserResponse.getCode());
        String username = validUserResponse.getData().getUsername();

        // TODO : 가게 정보 검색
        CommonDto<StoreResponseDto> validStoreFromApp = getValidStoreFromApp(storeId);
        if (validStoreFromApp == null || validStoreFromApp.getData() == null) {
            throw new BusinessLogicException(messageUtil.getMessage("api.call.server-error"));
        }
        throwByRespCode(validStoreFromApp.getCode());
        StoreResponseDto storeData = validStoreFromApp.getData();

        if ( !storeData.getOwnerName().equals(username)) {
            throw new NotValidRequestException(messageUtil.getMessage("app.airequest.not-valid-user"));
        }

        PageRequest pageable = JpaUtil.getNormalPageable(page, size, orderby, sort);
        Page<AiRequest> aiRequestPage;
        if (search == null || search.isBlank() || search.isEmpty()) {
            aiRequestPage = aiRequestQueryRepository.findAiRequestList(pageable, storeId);
        } else {
            aiRequestPage = aiRequestQueryRepository.findAiRequestListUsingSearch(pageable, storeId, search);
        }
        return AiRequestMapper.pageToGetListResponseDto(aiRequestPage, search);
    }

    @Transactional
    public AiRequestSaveResponseDto saveAiRequest (AiRequestSaveRequestDto requestDto, Long userId) {
        // TODO : 유저 검색
        CommonDto<UserResponseDto> validUserResponse = getValidUserFromApp(userId); // api 요청
        if (validUserResponse == null || validUserResponse.getData() == null) {
            throw new BusinessLogicException(messageUtil.getMessage("api.call.server-error"));
        }
        throwByRespCode(validUserResponse.getCode());
        String username = validUserResponse.getData().getUsername();

        // TODO : 가게 정보 검색
        CommonDto<StoreResponseDto> validStoreFromApp = getValidStoreFromApp(requestDto.getStoreId());
        if (validStoreFromApp == null || validStoreFromApp.getData() == null) {
            throw new BusinessLogicException(messageUtil.getMessage("api.call.server-error"));
        }
        throwByRespCode(validStoreFromApp.getCode());
        StoreResponseDto storeData = validStoreFromApp.getData();

        if (!storeData.getOwnerName().equals(username)) {
            throw new NotValidRequestException(messageUtil.getMessage("app.airequest.not-valid-user"));
        }

        String answer = callAiApi(requestDto.getQuestion());
        AiRequest aiRequest = AiRequestMapper.saveDtoToEntity(requestDto, answer);
        aiRequest.isCreatedBy(userId);
        aiRequestRepository.save(aiRequest);

        return AiRequestMapper.entityToSaveResponseDto(aiRequest);
    }

    // 외부 AI API 호출
    public String callAiApi (String question) {
        String targetUrl = apiUrl + apiKey;

        String body = questionBase.replace("{question}", question);

        JSONObject responseJsonObj = webClient.post()
                .uri(targetUrl)
                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .map(JSONObject::new)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) //에러 발생 시 2초 간격으로 최대 3회 재시도
                .onErrorResume(throwable -> {
                    log.error("Fail : {}", targetUrl, throwable);
                    return Mono.empty();
                })
                .block();

        String responseMessage = responseJsonObj
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text");

        return responseMessage;
    }

    // TODO : 하단 코드로 교체 예정
    private CommonDto<StoreResponseDto> getValidStoreFromApp(String storeId) {
        StoreResponseDto storeDataDto = StoreResponseDto.builder()
                .storeId(storeId)
                .storeName("김밥천국")
                .area("서울시 행복구 사랑로")
                .detailAddress("123-45")
                .ownerName("김사장")
                .build();

        return new CommonDto<>("SUCCESS", HttpStatus.OK.value(), storeDataDto);
    }

//     private CommonDto<StoreResponseDto> getValidStoreFromApp(String storeId) {
//         String targetUrl = STORE_APP_URL
//                 .replace("{host}", "localhost")
//                 .replace("{port}", "8082")
//                 .replace("{storeId}", storeId);
//         return webClient.get()
//                 .uri(targetUrl)
//                 .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                 .retrieve()
//                 .bodyToMono(new ParameterizedTypeReference<CommonDto<UserResponseDto>>() {})
//                 .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) //에러 발생 시 2초 간격으로 최대 3회 재시도
//                 .onErrorResume(throwable -> {
//                     log.error("Fail : {}", targetUrl, throwable);
//                     return Mono.empty();
//                 })
//                 .block();
//     }

    // TODO : 하단 코드로 교체 예정
    private CommonDto<UserResponseDto> getValidUserFromApp(Long userId) {
        String userName = "user" + userId;

        UserResponseDto userDto = UserResponseDto.builder()
                .username("김사장")
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
