package com.fortest.orderdelivery.app.domain.category.mapper;

import com.fortest.orderdelivery.app.domain.category.dto.CategoryDeleteResponseDto;
import com.fortest.orderdelivery.app.domain.category.dto.CategorySaveRequestDto;
import com.fortest.orderdelivery.app.domain.category.dto.CategorySaveResponseDto;
import com.fortest.orderdelivery.app.domain.category.dto.CategoryUpdateResponseDto;
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

    public static CategoryUpdateResponseDto toCategoryUpdateResponseDto(Category category) {
        return CategoryUpdateResponseDto.builder()
                .categoryId(category.getId())
                .categoryName(category.getName())
                .build();
    }

    public static CategoryDeleteResponseDto toCategoryDeleteResponseDto(Category category) {
        return CategoryDeleteResponseDto.builder()
                .categoryId(category.getId())
                .build();
    }
}
