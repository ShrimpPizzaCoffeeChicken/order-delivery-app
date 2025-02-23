package com.fortest.orderdelivery.app.domain.ai.controller;

import com.fortest.orderdelivery.app.domain.ai.dto.AiRequestGetListRequestDto;
import com.fortest.orderdelivery.app.domain.ai.dto.AiRequestGetListResponseDto;
import com.fortest.orderdelivery.app.domain.ai.dto.AiRequestSaveRequestDto;
import com.fortest.orderdelivery.app.domain.ai.dto.AiRequestSaveResponseDto;
import com.fortest.orderdelivery.app.domain.ai.service.AiRequestService;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.security.UserDetailsImpl;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/service")
@RequiredArgsConstructor
public class AiRequestServiceController {

    private final MessageUtil messageUtil;
    private final AiRequestService aiRequestService;

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/ai-requests")
    public ResponseEntity<CommonDto<AiRequestSaveResponseDto>> saveAiRequest(@Valid @RequestBody AiRequestSaveRequestDto requestDto,
                                                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        AiRequestSaveResponseDto responseDto = aiRequestService.saveAiRequest(requestDto, userDetails.getUser());
        return ResponseEntity.ok(
                CommonDto.<AiRequestSaveResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(responseDto)
                        .build()
        );
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/ai-requests")
    public ResponseEntity<CommonDto<AiRequestGetListResponseDto>> getAiRequestList(
            @Valid AiRequestGetListRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        AiRequestGetListResponseDto AiRequestList = aiRequestService.getAiRequestList(
            requestDto.getStoreId(),
            requestDto.getPage(),
            requestDto.getSize(),
            requestDto.getOrderby(),
            requestDto.getSort(),
            requestDto.getSearch(),
            userDetails.getUser()
    );
        return ResponseEntity.ok(
                CommonDto.<AiRequestGetListResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(AiRequestList)
                        .build()
        );
    }

}
