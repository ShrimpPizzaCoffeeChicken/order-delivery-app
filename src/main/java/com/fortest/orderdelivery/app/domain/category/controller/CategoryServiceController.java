package com.fortest.orderdelivery.app.domain.category.controller;

import com.fortest.orderdelivery.app.domain.category.dto.CategorySaveRequestDto;
import com.fortest.orderdelivery.app.domain.category.dto.CategorySaveResponseDto;
import com.fortest.orderdelivery.app.domain.category.dto.CategoryUpdateRequestDto;
import com.fortest.orderdelivery.app.domain.category.dto.CategoryUpdateResponseDto;
import com.fortest.orderdelivery.app.domain.category.service.CategoryService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/service")
@RequiredArgsConstructor
public class CategoryServiceController {

    private final CategoryService categoryService;

    @PostMapping("/categories")
    public ResponseEntity<CommonDto<CategorySaveResponseDto>> saveCategory(@RequestBody CategorySaveRequestDto categorySaveRequestDto) {
        // TODO : TEMP : userId 를 UserDetail 에서 획득해야함
        CategorySaveResponseDto categorySaveResponseDto = categoryService.saveCategory(categorySaveRequestDto, 123L);

        return ResponseEntity.ok(
                CommonDto.<CategorySaveResponseDto>builder()
                        .message("SUCCESS")
                        .code(HttpStatus.OK.value())
                        .data(categorySaveResponseDto)
                        .build()
        );
    }

    @PatchMapping("/categories/{categoryId}")
    public ResponseEntity<CommonDto<CategoryUpdateResponseDto>> updateCategory(@PathVariable String categoryId, @RequestBody CategoryUpdateRequestDto categoryUpdateRequestDto){
        CategoryUpdateResponseDto categoryUpdateResponseDto = categoryService.updateCategory(categoryId, categoryUpdateRequestDto);

        return ResponseEntity.ok(
                CommonDto.<CategoryUpdateResponseDto>builder()
                        .message("SUCCESS")
                        .code(HttpStatus.OK.value())
                        .data(categoryUpdateResponseDto)
                        .build()
        );
    }
}
