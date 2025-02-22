package com.fortest.orderdelivery.app.domain.ai.controller;

import com.fortest.orderdelivery.app.domain.ai.dto.AiRequestGetListResponseDto;
import com.fortest.orderdelivery.app.domain.ai.dto.AiRequestSaveRequestDto;
import com.fortest.orderdelivery.app.domain.ai.dto.AiRequestSaveResponseDto;
import com.fortest.orderdelivery.app.domain.ai.service.AiRequestService;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/service")
@RestController
public class AiRequestServiceController {

    private final MessageUtil messageUtil;
    private final AiRequestService aiRequestService;

    @PostMapping("/ai-requests")
    public ResponseEntity<CommonDto<AiRequestSaveResponseDto>> saveAiRequest(
            @RequestBody AiRequestSaveRequestDto requestDto
    ) {
        // TODO : 유저 id 획득
        AiRequestSaveResponseDto responseDto = aiRequestService.saveAiRequest(requestDto, new User());
        return ResponseEntity.ok(
                CommonDto.<AiRequestSaveResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(responseDto)
                        .build()
        );
    }

    @GetMapping("/ai-requests")
    public ResponseEntity<CommonDto<AiRequestGetListResponseDto>> getAiRequestList(
            @RequestParam("store-id") String storeId,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam("orderby") String orderby,
            @RequestParam("sort") String sort,
            @RequestParam("search") String search
    ) {
        // TODO : 유저 id 획득
        AiRequestGetListResponseDto responseDto = aiRequestService.getAiRequestList(storeId, page, size, orderby, sort, search, new User());
        return ResponseEntity.ok(
                CommonDto.<AiRequestGetListResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(responseDto)
                        .build()
        );
    }

}
