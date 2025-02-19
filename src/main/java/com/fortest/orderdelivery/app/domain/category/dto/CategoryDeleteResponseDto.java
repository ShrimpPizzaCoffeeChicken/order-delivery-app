package com.fortest.orderdelivery.app.domain.category.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class CategoryDeleteResponseDto {
    @JsonProperty("category-id")
    private String categoryId;

}
