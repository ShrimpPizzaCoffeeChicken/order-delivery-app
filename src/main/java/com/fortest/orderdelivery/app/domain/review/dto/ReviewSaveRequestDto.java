package com.fortest.orderdelivery.app.domain.review.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewSaveRequestDto {

    @JsonProperty("store-id")
    private String storeId;

    @JsonProperty("order-id")
    private String orderId;

    @JsonProperty("rate")
    private Long rate;

    @JsonProperty("contents")
    private String contents;
}
