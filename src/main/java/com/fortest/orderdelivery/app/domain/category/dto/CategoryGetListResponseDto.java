package com.fortest.orderdelivery.app.domain.category.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryGetListResponseDto {
    @JsonProperty("total-contents")
    private Long totalContents;

    private Integer size;

    @JsonProperty("current-page")
    private Integer currentPage;

    @JsonProperty("category-list")
    private List<CategoryDto> categoryList;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryDto {
        @JsonProperty("category-id")
        private String categoryId;
        @JsonProperty("category-name")
        private String categoryName;
    }
}
