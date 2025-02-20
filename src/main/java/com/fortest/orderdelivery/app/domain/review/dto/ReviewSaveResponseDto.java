package com.fortest.orderdelivery.app.domain.review.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewSaveResponseDto {

    @JsonProperty("review-id")
    private String reviewId;
}
