package com.fortest.orderdelivery.app.domain.review.service;

import com.fortest.orderdelivery.app.domain.review.dto.*;
import com.fortest.orderdelivery.app.domain.review.entity.Review;
import com.fortest.orderdelivery.app.domain.review.mapper.ReviewMapper;
import com.fortest.orderdelivery.app.domain.review.repository.ReviewQueryRepository;
import com.fortest.orderdelivery.app.domain.review.repository.ReviewRepository;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.gateway.ApiGateway;
import com.fortest.orderdelivery.app.global.util.JpaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ApiGateway apiGateway;
    private final ReviewRepository reviewRepository;
    private final ReviewQueryRepository reviewQueryRepository;
    private final MessageSource messageSource;

    @Transactional
    public ReviewSaveResponseDto saveReview(ReviewSaveRequestDto reviewSaveRequestDto, User user) {

        Review newReview = ReviewMapper.toReview(reviewSaveRequestDto);
        newReview.isCreatedBy(user.getId());
        Review savedReview = reviewRepository.save(newReview);

        return ReviewMapper.toReviewSaveResponseDto(savedReview);
    }

    public ReviewGetListDto getReviewList(String storeId, Integer page, Integer size, String orderby, String sort) {

        PageRequest pageable = JpaUtil.getNormalPageable(page, size, orderby, sort);
        Page<Review> reviewList;

        reviewList = reviewQueryRepository.findReviewList(storeId, pageable);
        return ReviewMapper.pageToGetReviewListDto(reviewList);
    }

    @Transactional
    public ReviewDeleteResponseDto deleteReview(String reviewId, User user){
        Review review = reviewRepository.findById(reviewId).orElseThrow(()->
                new BusinessLogicException(messageSource.getMessage("api.call.client-error",null, Locale.KOREA)));
        review.isDeletedNow(user.getId());

        return ReviewMapper.toReviewDeleteResponseDto(review);
    }

    public ReviewGetResponseDto getReview(String reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(()->
                new BusinessLogicException(messageSource.getMessage("api.call.client-error",null, Locale.KOREA)));

        OrderDetailsResponseDto commonDto = apiGateway.getOrderDetailsFromApp(review.getOrderId());

        return ReviewMapper.toReviewGetResponseDto(review, commonDto);
    }
}
