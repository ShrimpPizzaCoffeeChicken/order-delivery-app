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
import com.fortest.orderdelivery.app.domain.user.entity.RoleType;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.exception.NotValidRequestException;
import com.fortest.orderdelivery.app.global.gateway.ApiGateway;
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

    private final ApiGateway apiGateway;
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
     * @param user : 접속한 유저
     * @return
     */
    @Transactional
    public AiRequestGetListResponseDto getAiRequestList(String storeId, Integer page, Integer size, String orderby, String sort, String search, User user) {
        StoreResponseDto validStoreDto = apiGateway.getValidStoreFromApp(storeId);

        if (user.getRoleType().getRoleName() == RoleType.RoleName.CUSTOMER
            || user.getRoleType().getRoleName() == RoleType.RoleName.OWNER) {
            if ( !validStoreDto.getOwnerName().equals(user.getUsername())) {
                throw new NotValidRequestException(messageUtil.getMessage("app.airequest.not-valid-user"));
            }
        }

        PageRequest pageable = JpaUtil.getNormalPageable(page, size, orderby, sort);
        Page<AiRequest> aiRequestPage;
        aiRequestPage = aiRequestQueryRepository.findAiRequestListUsingSearch(pageable, storeId, search);
        return AiRequestMapper.pageToGetListResponseDto(aiRequestPage, search);
    }

    @Transactional
    public AiRequestSaveResponseDto saveAiRequest (AiRequestSaveRequestDto requestDto, User user) {
        StoreResponseDto validStoreDto = apiGateway.getValidStoreFromApp(requestDto.getStoreId());

        if (!validStoreDto.getOwnerName().equals(user.getUsername())) {
            throw new NotValidRequestException(messageUtil.getMessage("app.airequest.not-valid-user"));
        }

        String answer = callAiApi(requestDto.getQuestion());
        AiRequest aiRequest = AiRequestMapper.saveDtoToEntity(requestDto, answer);
        aiRequest.isCreatedBy(user.getId());
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
}
