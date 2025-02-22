package com.fortest.orderdelivery.app.domain.category.mapper;

import com.fortest.orderdelivery.app.domain.category.dto.*;
import com.fortest.orderdelivery.app.domain.category.entity.Category;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryMapper {

    public static Category categorySaveRequestDtoToEntity(CategorySaveRequestDto categorySaveRequestDto) {
        return Category.builder()
                .name(categorySaveRequestDto.getCategoryName())
                .build();
    }

    public static CategorySaveResponseDto entityToCategorySaveResponseDto(Category category) {
        return CategorySaveResponseDto.builder()
            .categoryId(category.getId())
            .categoryName(category.getName())
            .build();
    }

    public static CategoryGetListDto pageToGetCategoryListDto(Page<Category> page) {
        CategoryGetListDto.CategoryGetListDtoBuilder builder = CategoryGetListDto.builder();
        builder = builder
                .totalContents(page.getTotalElements())
                .size(page.getSize())
                .currentPage(page.getNumber() + 1);
        List<CategoryGetListDto.CategoryDto> categoryDtoList = page.getContent().stream()
                .map(CategoryMapper::entityToCategoryListDtoElement)
                .collect(Collectors.toList());
        builder = builder.categoryList(categoryDtoList);
        return builder.build();
    }

    public static CategoryGetListDto.CategoryDto entityToCategoryListDtoElement(Category category) {
        return CategoryGetListDto.CategoryDto.builder()
                .categoryId(category.getId())
                .categoryName(category.getName())
                .build();
    }

    public static CategoryUpdateResponseDto entityToCategoryUpdateResponseDto(Category category) {
        return CategoryUpdateResponseDto.builder()
                .categoryId(category.getId())
                .categoryName(category.getName())
                .build();
    }

    public static CategoryDeleteResponseDto entityToCategoryDeleteResponseDto(Category category) {
        return CategoryDeleteResponseDto.builder()
                .categoryId(category.getId())
                .build();
    }
}
