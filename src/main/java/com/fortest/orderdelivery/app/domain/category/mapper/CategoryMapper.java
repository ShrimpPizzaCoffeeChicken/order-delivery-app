package com.fortest.orderdelivery.app.domain.category.mapper;

import com.fortest.orderdelivery.app.domain.category.dto.CategorySaveRequestDto;
import com.fortest.orderdelivery.app.domain.category.dto.CategorySaveResponseDto;
import com.fortest.orderdelivery.app.domain.category.entity.Category;

public class CategoryMapper {

    public static Category toCategory(CategorySaveRequestDto categorySaveRequestDto) {
        return Category.builder()
                .id(categorySaveRequestDto.getCategoryId())
                .name(categorySaveRequestDto.getCategoryName())
                .build();
    }

    public static CategorySaveResponseDto toCategorySaveResponseDto(Category category) {
        return CategorySaveResponseDto.builder()
            .categoryId(category.getId())
            .categoryName(category.getName())
            .build();
    }
}
