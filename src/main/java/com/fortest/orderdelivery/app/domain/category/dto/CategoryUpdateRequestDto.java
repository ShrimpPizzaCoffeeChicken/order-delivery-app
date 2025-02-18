package com.fortest.orderdelivery.app.domain.category.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryUpdateRequestDto {

    @JsonProperty("category-name")
    private String categoryName;
}
