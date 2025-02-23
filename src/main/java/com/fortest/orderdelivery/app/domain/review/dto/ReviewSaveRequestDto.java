package com.fortest.orderdelivery.app.domain.review.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewSaveRequestDto {

    @Size(min = 1, max = 100)
    @JsonProperty("store-id")
    private String storeId;

    @Size(min = 1, max = 50)
    @JsonProperty("order-id")
    private String orderId;

    @Size(min = 1, max = 5)
    @JsonProperty("rate")
    private Long rate;

    @JsonProperty("contents")
    private String contents;
}
