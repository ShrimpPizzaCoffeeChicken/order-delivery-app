package com.fortest.orderdelivery.app.domain.category.controller;

import com.fortest.orderdelivery.app.domain.category.dto.*;
import com.fortest.orderdelivery.app.domain.category.service.CategoryService;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/service")
@RequiredArgsConstructor
public class CategoryServiceController {

    private final MessageUtil messageUtil;
    private final CategoryService categoryService;

    @PostMapping("/categories")
    public ResponseEntity<CommonDto<CategorySaveResponseDto>> saveCategory(@RequestBody CategorySaveRequestDto categorySaveRequestDto) {
        // TODO : TEMP : userId 를 UserDetail 에서 획득해야함
        CategorySaveResponseDto categorySaveResponseDto = categoryService.saveCategory(categorySaveRequestDto, new User());

        return ResponseEntity.ok(
                CommonDto.<CategorySaveResponseDto>builder()
                        .message(messageUtil.getSuccessMessage())
                        .code(HttpStatus.OK.value())
                        .data(categorySaveResponseDto)
                        .build()
        );
    }

    @GetMapping("/categories")
    public ResponseEntity<CommonDto<CategoryGetListDto>> getCategoryList(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam("orderby") String orderby,
            @RequestParam("sort") String sort
    ) {
        CategoryGetListDto categoryList = categoryService.getCategoryList(page, size, orderby, sort);

        return ResponseEntity.ok(
                CommonDto.<CategoryGetListDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(categoryList)
                        .build()
        );
    }

    @PatchMapping("/categories/{categoryId}")
    public ResponseEntity<CommonDto<CategoryUpdateResponseDto>> updateCategory(@PathVariable String categoryId, @RequestBody CategoryUpdateRequestDto categoryUpdateRequestDto){
        CategoryUpdateResponseDto categoryUpdateResponseDto = categoryService.updateCategory(categoryId, categoryUpdateRequestDto, new User());

        return ResponseEntity.ok(
                CommonDto.<CategoryUpdateResponseDto>builder()
                        .message(messageUtil.getSuccessMessage())
                        .code(HttpStatus.OK.value())
                        .data(categoryUpdateResponseDto)
                        .build()
        );
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<CommonDto<CategoryDeleteResponseDto>> deleteCategory(@PathVariable String categoryId){
        CategoryDeleteResponseDto categoryDeleteResponseDto = categoryService.deleteCategory(categoryId, 123L);

        return ResponseEntity.ok(
                CommonDto.<CategoryDeleteResponseDto>builder()
                        .message(messageUtil.getSuccessMessage())
                        .code(HttpStatus.OK.value())
                        .data(categoryDeleteResponseDto)
                        .build()
        );
    }
}
