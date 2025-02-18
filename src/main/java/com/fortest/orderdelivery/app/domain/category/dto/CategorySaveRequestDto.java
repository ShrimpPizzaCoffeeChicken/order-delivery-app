package com.fortest.orderdelivery.app.domain.category.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CategorySaveRequestDto {

    @JsonProperty("category-id")
    private String categoryId;

    @JsonProperty("category-name")
    private String categoryName;

}
