package com.fortest.orderdelivery.app.domain.review.mapper;

import com.fortest.orderdelivery.app.domain.review.dto.*;
import com.fortest.orderdelivery.app.domain.review.entity.Review;
import com.fortest.orderdelivery.app.global.util.CommonUtil;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class ReviewMapper {
    public static Review reviewSaveRequestDtoToEntity(ReviewSaveRequestDto reviewSaveRequestDto) {
        return Review.builder()
                .storeId(reviewSaveRequestDto.getStoreId())
                .orderId(reviewSaveRequestDto.getOrderId())
                .rate(reviewSaveRequestDto.getRate())
                .contents(reviewSaveRequestDto.getContents())
                .build();
    }

    public static ReviewSaveResponseDto entityToReviewSaveResponseDto(Review review) {
        return ReviewSaveResponseDto.builder()
                .reviewId(review.getId())
                .build();
    }

    public static ReviewGetListResponseDto pageToGetReviewListDto(Page<Review> page) {
        ReviewGetListResponseDto.ReviewGetListResponseDtoBuilder builder = ReviewGetListResponseDto.builder();
        builder = builder
                .totalContents(page.getTotalElements())
                .size(page.getSize())
                .currentPage(page.getNumber() + 1);
        List<ReviewGetListResponseDto.ReviewDto> reviewDtoList = page.getContent().stream()
                .map(ReviewMapper::entityToReviewListDtoElement)
                .collect(Collectors.toList());
        builder = builder.reviewList(reviewDtoList);
        return builder.build();
    }

    public static ReviewGetListResponseDto.ReviewDto entityToReviewListDtoElement(Review review) {
        return ReviewGetListResponseDto.ReviewDto.builder()
                .rate(review.getRate())
                .contents(review.getContents())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    public static ReviewDeleteResponseDto entityToReviewDeleteResponseDto(Review review) {
        return ReviewDeleteResponseDto.builder()
                .reviewId(review.getId())
                .build();
    }

    public static ReviewGetResponseDto entityToReviewGetResponseDto(Review review, OrderDetailsResponseDto responseDto) {
        return ReviewGetResponseDto.builder()
            .storeId(review.getStoreId())
            .storeName(review.getStoreName())
            .contents(review.getContents())
            .rate(review.getRate())
            .createdAt(CommonUtil.LDTToString(review.getCreatedAt()))
            .updatedAt(CommonUtil.LDTToString(review.getUpdatedAt()))
            .menuList(responseDto.getMenuList())
            .build();
    }
}
