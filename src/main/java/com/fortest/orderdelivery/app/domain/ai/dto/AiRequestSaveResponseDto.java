package com.fortest.orderdelivery.app.domain.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiRequestSaveResponseDto {

    @JsonProperty("ai-request-id")
    private String aiRequestId;
    @JsonProperty("store-id")
    private String storeId;

    private String question;
    private String answer;
}
