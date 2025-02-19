package com.fortest.orderdelivery.app.domain.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles({"develop"})
@SpringBootTest
class AiRequestServiceTest {

    @Autowired
    AiRequestService aiRequestService;

    @Test
    @DisplayName("외부 AI 요청 테스트 : 약 2초 정도 소요")
    void callAiApiTest () {
        String answerMessage = aiRequestService.callAiApi("가게 메뉴로 등록할 햄버거 메뉴에 대한 설명을 추천해줘");
        log.info("answerMessage = {}", answerMessage);
    }

}