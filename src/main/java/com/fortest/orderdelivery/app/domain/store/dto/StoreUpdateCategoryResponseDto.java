package com.fortest.orderdelivery.app.domain.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreUpdateCategoryResponseDto {

    private List<CategoryDto> categoryList;

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryDto {
        private String categoryId;
        private String name;
    }
}
