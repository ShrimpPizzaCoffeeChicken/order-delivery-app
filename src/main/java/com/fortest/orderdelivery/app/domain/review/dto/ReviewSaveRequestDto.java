package com.fortest.orderdelivery.app.domain.review.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @Min(1)
    @Max(5)
    @JsonProperty("rate")
    private Integer rate;

    @JsonProperty("contents")
    private String contents;
}
