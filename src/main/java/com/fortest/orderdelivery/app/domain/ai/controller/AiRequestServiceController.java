package com.fortest.orderdelivery.app.domain.ai.controller;

import com.fortest.orderdelivery.app.domain.ai.dto.AiRequestSaveRequestDto;
import com.fortest.orderdelivery.app.domain.ai.dto.AiRequestSaveResponseDto;
import com.fortest.orderdelivery.app.domain.ai.service.AiRequestService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/service")
@RestController
public class AiRequestServiceController {

    private final AiRequestService aiRequestService;

    @PostMapping("/ai-request")
    public ResponseEntity<CommonDto<AiRequestSaveResponseDto>> saveAiRequest(
            @RequestBody AiRequestSaveRequestDto requestDto
    ) {
        // TODO : 유저 id 획득
        AiRequestSaveResponseDto responseDto = aiRequestService.saveAiRequest(requestDto, 123L);
        return ResponseEntity.ok(
                CommonDto.<AiRequestSaveResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message("Success")
                        .data(responseDto)
                        .build()
        );
    }

}
