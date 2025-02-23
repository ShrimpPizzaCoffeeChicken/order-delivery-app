package com.fortest.orderdelivery.app.domain.category.controller;

import com.fortest.orderdelivery.app.domain.category.dto.*;
import com.fortest.orderdelivery.app.domain.category.service.CategoryService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.security.UserDetailsImpl;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/service")
@RequiredArgsConstructor
public class CategoryServiceController {

    private final MessageUtil messageUtil;
    private final CategoryService categoryService;

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/categories")
    public ResponseEntity<CommonDto<CategorySaveResponseDto>> saveCategory(@Valid @RequestBody CategorySaveRequestDto categorySaveRequestDto,
                                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CategorySaveResponseDto categorySaveResponseDto = categoryService.saveCategory(categorySaveRequestDto, userDetails.getUser());

        return ResponseEntity.ok(
                CommonDto.<CategorySaveResponseDto>builder()
                        .message(messageUtil.getSuccessMessage())
                        .code(HttpStatus.OK.value())
                        .data(categorySaveResponseDto)
                        .build()
        );
    }

    @PreAuthorize("hasRole('CUSTOMER') or hasRole('OWNER') or hasRole('MANAGER') or hasRole('MASTER')")
    @GetMapping("/categories")
    public ResponseEntity<CommonDto<CategoryGetListResponseDto>> getCategoryList(
            @Valid CategoryGetListRequestDto requestDto
    ) {
        CategoryGetListResponseDto categoryList = categoryService.getCategoryList(
                requestDto.getPage(),
                requestDto.getSize(),
                requestDto.getOrderby(),
                requestDto.getSort()
        );

        return ResponseEntity.ok(
                CommonDto.<CategoryGetListResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(categoryList)
                        .build()
        );
    }

    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/categories/{categoryId}")
    public ResponseEntity<CommonDto<CategoryUpdateResponseDto>> updateCategory(@PathVariable String categoryId,
                                                                               @RequestBody CategoryUpdateRequestDto categoryUpdateRequestDto,
                                                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CategoryUpdateResponseDto categoryUpdateResponseDto = categoryService.updateCategory(categoryId, categoryUpdateRequestDto, userDetails.getUser());

        return ResponseEntity.ok(
                CommonDto.<CategoryUpdateResponseDto>builder()
                        .message(messageUtil.getSuccessMessage())
                        .code(HttpStatus.OK.value())
                        .data(categoryUpdateResponseDto)
                        .build()
        );
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<CommonDto<CategoryDeleteResponseDto>> deleteCategory(@PathVariable String categoryId,
                                                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CategoryDeleteResponseDto categoryDeleteResponseDto = categoryService.deleteCategory(categoryId,  userDetails.getUser());

        return ResponseEntity.ok(
                CommonDto.<CategoryDeleteResponseDto>builder()
                        .message(messageUtil.getSuccessMessage())
                        .code(HttpStatus.OK.value())
                        .data(categoryDeleteResponseDto)
                        .build()
        );
    }
}
