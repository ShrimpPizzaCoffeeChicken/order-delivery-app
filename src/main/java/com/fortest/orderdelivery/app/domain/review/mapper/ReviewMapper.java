package com.fortest.orderdelivery.app.domain.review.mapper;

import com.fortest.orderdelivery.app.domain.review.dto.ReviewSaveRequestDto;
import com.fortest.orderdelivery.app.domain.review.dto.ReviewSaveResponseDto;
import com.fortest.orderdelivery.app.domain.review.entity.Review;

public class ReviewMapper {
    public static Review toReview(ReviewSaveRequestDto reviewSaveRequestDto) {
        return Review.builder()
                .storeId(reviewSaveRequestDto.getStoreId())
                .orderId(reviewSaveRequestDto.getOrderId())
                .rate(reviewSaveRequestDto.getRate())
                .contents(reviewSaveRequestDto.getContents())
                .build();
    }

    public static ReviewSaveResponseDto toReviewSaveResponseDto(Review review) {
        return ReviewSaveResponseDto.builder()
                .reviewId(review.getId())
                .build();
    }
}
