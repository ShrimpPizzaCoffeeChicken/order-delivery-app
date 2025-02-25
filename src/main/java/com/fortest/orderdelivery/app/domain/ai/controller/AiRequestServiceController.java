package com.fortest.orderdelivery.app.domain.ai.controller;

import com.fortest.orderdelivery.app.domain.ai.dto.AiRequestGetListRequestDto;
import com.fortest.orderdelivery.app.domain.ai.dto.AiRequestGetListResponseDto;
import com.fortest.orderdelivery.app.domain.ai.dto.AiRequestSaveRequestDto;
import com.fortest.orderdelivery.app.domain.ai.dto.AiRequestSaveResponseDto;
import com.fortest.orderdelivery.app.domain.ai.service.AiRequestService;
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

    @PreAuthorize("hasRole('OWNER') or hasRole('MANAGER') or hasRole('MASTER')")
    @GetMapping("/ai-requests")
    public ResponseEntity<CommonDto<AiRequestGetListResponseDto>> getAiRequestList(
            @RequestParam("store-id") String storeId,
            @Valid AiRequestGetListRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        AiRequestGetListResponseDto AiRequestList = aiRequestService.getAiRequestList(
            storeId,
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
