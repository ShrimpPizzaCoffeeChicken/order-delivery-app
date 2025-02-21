package com.fortest.orderdelivery.app.domain.review.mapper;

import com.fortest.orderdelivery.app.domain.review.dto.OrderDetailsResponseDto;
import com.fortest.orderdelivery.app.domain.review.dto.ReviewDeleteResponseDto;
import com.fortest.orderdelivery.app.domain.review.dto.ReviewGetListDto;
import com.fortest.orderdelivery.app.domain.review.dto.ReviewGetResponseDto;
import com.fortest.orderdelivery.app.domain.review.dto.ReviewSaveRequestDto;
import com.fortest.orderdelivery.app.domain.review.dto.ReviewSaveResponseDto;
import com.fortest.orderdelivery.app.domain.review.entity.Review;
import com.fortest.orderdelivery.app.global.util.CommonUtil;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

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

    public static ReviewGetListDto pageToGetReviewListDto(Page<Review> page) {
        ReviewGetListDto.ReviewGetListDtoBuilder builder = ReviewGetListDto.builder();
        builder = builder
                .totalContents(page.getTotalElements())
                .size(page.getSize())
                .currentPage(page.getNumber() + 1);
        List<ReviewGetListDto.ReviewDto> reviewDtoList = page.getContent().stream()
                .map(ReviewMapper::entityToReviewListDtoElement)
                .collect(Collectors.toList());
        builder = builder.reviewList(reviewDtoList);
        return builder.build();
    }

    public static ReviewGetListDto.ReviewDto entityToReviewListDtoElement(Review review) {
        return ReviewGetListDto.ReviewDto.builder()
                .rate(review.getRate())
                .contents(review.getContents())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    public static ReviewDeleteResponseDto toReviewDeleteResponseDto(Review review) {
        return ReviewDeleteResponseDto.builder()
                .reviewId(review.getId())
                .build();
    }

    public static ReviewGetResponseDto toReviewGetResponseDto(Review review, OrderDetailsResponseDto responseDto) {
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
