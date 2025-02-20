package com.fortest.orderdelivery.app.domain.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiRequestGetListResponseDto {

    private String search;

    @JsonProperty("total-contents")
    private Long totalContents;

    private Integer size;

    @JsonProperty("current-page")
    private Integer currentPage;

    @JsonProperty("ai-request-list")
    private List<AiRequestDto> aiRequestList;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AiRequestDto {
        @JsonProperty("ai-request-id")
        private String aiRequestId;
        @JsonProperty("store-id")
        private String storeId;
        private String question;
        private String answer;
        @JsonProperty("created-at")
        private String createdAt;
    }
}
