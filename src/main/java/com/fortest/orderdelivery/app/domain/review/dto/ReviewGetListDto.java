package com.fortest.orderdelivery.app.domain.review.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewGetListDto {
    @JsonProperty("total-contents")
    private Long totalContents;

    private Integer size;

    @JsonProperty("current-page")
    private Integer currentPage;

    @JsonProperty("review-list")
    private List<ReviewDto> reviewList;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReviewDto {

        private Long rate;

        private String contents;

        @JsonProperty("create-at")
        private LocalDateTime createdAt;

        @JsonProperty("update-at")
        private LocalDateTime updatedAt;

    }
}
