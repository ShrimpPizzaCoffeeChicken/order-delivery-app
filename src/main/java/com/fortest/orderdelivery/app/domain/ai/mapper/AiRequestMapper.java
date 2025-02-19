package com.fortest.orderdelivery.app.domain.ai.mapper;

import com.fortest.orderdelivery.app.domain.ai.dto.AiRequestSaveRequestDto;
import com.fortest.orderdelivery.app.domain.ai.dto.AiRequestSaveResponseDto;
import com.fortest.orderdelivery.app.domain.ai.entity.AiRequest;

public class AiRequestMapper {

    public static AiRequest saveDtoToEntity(AiRequestSaveRequestDto requestDto, String answer) {
        return AiRequest.builder()
                .question(requestDto.getQuestion())
                .answer(answer)
                .storeId(requestDto.getStoreId())
                .build();
    }

    public static AiRequestSaveResponseDto entityToSaveResponseDto(AiRequest aiRequest) {
        return AiRequestSaveResponseDto.builder()
                .aiRequestId(aiRequest.getId())
                .storeId(aiRequest.getStoreId())
                .question(aiRequest.getQuestion())
                .answer(aiRequest.getAnswer())
                .build();
    }
}
