package com.fortest.orderdelivery.app.domain.category.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategorySaveRequestDto {

    @Size(min = 1, max = 100, message = "카테고리 이름은 필수 입력값입니다.")
    @JsonProperty("category-name")
    private String categoryName;

}
