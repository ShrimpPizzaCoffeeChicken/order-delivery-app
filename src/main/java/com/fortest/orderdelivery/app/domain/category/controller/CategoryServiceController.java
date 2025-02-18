package com.fortest.orderdelivery.app.domain.category.controller;

import com.fortest.orderdelivery.app.domain.category.dto.CategorySaveRequestDto;
import com.fortest.orderdelivery.app.domain.category.dto.CategorySaveResponseDto;
import com.fortest.orderdelivery.app.domain.category.entity.Category;
import com.fortest.orderdelivery.app.domain.category.service.CategoryService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service")
@RequiredArgsConstructor
public class CategoryServiceController {

    private final CategoryService categoryService;

    @PostMapping("/categories")
    public ResponseEntity<CommonDto<CategorySaveResponseDto>> saveCategory(@RequestBody CategorySaveRequestDto categorySaveRequestDto) {
        // TODO : TEMP : userId 를 UserDetail 에서 획득해야함
        Category category = categoryService.saveCategory(categorySaveRequestDto, 123L);
        CategorySaveResponseDto categorySaveResponseDto = CategorySaveResponseDto.builder()
                .categoryId(category.getId())
                .categoryName(category.getName())
                .build();

        return ResponseEntity.ok(
                CommonDto.<CategorySaveResponseDto>builder()
                        .message("SUCCESS")
                        .code(HttpStatus.OK.value())
                        .data(categorySaveResponseDto)
                        .build()

        );
    }

}
