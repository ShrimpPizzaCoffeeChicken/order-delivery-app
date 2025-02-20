package com.fortest.orderdelivery.app.domain.review.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class ReviewDeleteResponseDto {
    @JsonProperty("review-id")
    private String reviewId;
}
