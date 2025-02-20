package com.fortest.orderdelivery.app.domain.ai.mapper;

import com.fortest.orderdelivery.app.domain.ai.dto.AiRequestGetListResponseDto;
import com.fortest.orderdelivery.app.domain.ai.dto.AiRequestSaveRequestDto;
import com.fortest.orderdelivery.app.domain.ai.dto.AiRequestSaveResponseDto;
import com.fortest.orderdelivery.app.domain.ai.entity.AiRequest;
import com.fortest.orderdelivery.app.global.util.CommonUtil;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

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

    public static AiRequestGetListResponseDto pageToGetListResponseDto(Page<AiRequest> page, String search) {
        AiRequestGetListResponseDto.AiRequestGetListResponseDtoBuilder builder = AiRequestGetListResponseDto.builder()
                .search(search == null ? "" : search)
                .totalContents(page.getTotalElements())
                .size(page.getSize())
                .currentPage(page.getNumber() + 1);
        List<AiRequestGetListResponseDto.AiRequestDto> aiRequestDtos = page.getContent().stream()
                .map(AiRequestMapper::entityToGetListDtoElement)
                .collect(Collectors.toList());
        return builder.aiRequestList(aiRequestDtos).build();
    }

    public static AiRequestGetListResponseDto.AiRequestDto entityToGetListDtoElement(AiRequest aiRequest) {
        return AiRequestGetListResponseDto.AiRequestDto.builder()
                .aiRequestId(aiRequest.getId())
                .storeId(aiRequest.getStoreId())
                .question(aiRequest.getQuestion())
                .answer(aiRequest.getQuestion())
                .createdAt(CommonUtil.LDTToString(aiRequest.getCreatedAt()))
                .build();
    }
}
