package com.fortest.orderdelivery.app.domain.category.service;

import com.fortest.orderdelivery.app.domain.category.dto.*;
import com.fortest.orderdelivery.app.domain.category.entity.Category;
import com.fortest.orderdelivery.app.domain.category.mapper.CategoryMapper;
import com.fortest.orderdelivery.app.domain.category.repository.CategoryQueryRepository;
import com.fortest.orderdelivery.app.domain.category.repository.CategoryRepository;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.util.JpaUtil;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryQueryRepository categoryQueryRepository;
    private final MessageUtil messageUtil;

    @Transactional
    public CategorySaveResponseDto saveCategory(CategorySaveRequestDto categorySaveRequestDto, User user) {

        Category newCategory = CategoryMapper.categorySaveRequestDtoToEntity(categorySaveRequestDto);
        newCategory.isCreatedBy(user.getId());
        Category savedCategory = categoryRepository.save(newCategory);

        return CategoryMapper.entityToCategorySaveResponseDto(savedCategory);
    }

    public CategoryGetListResponseDto getCategoryList(Integer page, Integer size, String orderby, String sort) {

        PageRequest pageable = JpaUtil.getNormalPageable(page, size, orderby, sort);
        Page<Category> categoryPage = categoryQueryRepository.findCategoryList(pageable);

        return CategoryMapper.pageToGetCategoryListDto(categoryPage);
    }

    @Transactional
    public CategoryUpdateResponseDto updateCategory(String categoryId, CategoryUpdateRequestDto categoryUpdateRequestDto, User user){

        String categoryName = categoryUpdateRequestDto.getCategoryName();

        Category category = categoryRepository.findById(categoryId).orElseThrow(()->
                new BusinessLogicException(messageUtil.getMessage( "api.call.client-error")));

        category.update(categoryName);

        category.isUpdatedNow(user.getId());

        return CategoryMapper.entityToCategoryUpdateResponseDto(category);
    }

    @Transactional
    public CategoryDeleteResponseDto deleteCategory(String categoryId, User user){
        Category category = categoryRepository.findById(categoryId).orElseThrow(()->
                new BusinessLogicException(messageUtil.getMessage("api.call.client-error")));

        category.isDeletedNow(user.getId());

        return CategoryMapper.entityToCategoryDeleteResponseDto(category);
    }
}
