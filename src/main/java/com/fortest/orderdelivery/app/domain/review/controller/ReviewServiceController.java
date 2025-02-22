package com.fortest.orderdelivery.app.domain.review.controller;

import com.fortest.orderdelivery.app.domain.review.dto.ReviewDeleteResponseDto;
import com.fortest.orderdelivery.app.domain.review.dto.ReviewGetListDto;
import com.fortest.orderdelivery.app.domain.review.dto.ReviewGetResponseDto;
import com.fortest.orderdelivery.app.domain.review.dto.ReviewSaveRequestDto;
import com.fortest.orderdelivery.app.domain.review.dto.ReviewSaveResponseDto;
import com.fortest.orderdelivery.app.domain.review.service.ReviewService;
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
public class ReviewServiceController {

    private final MessageUtil messageUtil;
    private final ReviewService reviewService;

    @PostMapping("/reviews")
    public ResponseEntity<CommonDto<ReviewSaveResponseDto>> saveReview(@RequestBody ReviewSaveRequestDto reviewSaveRequestDto) {
        // TODO : TEMP : userId 를 UserDetail 에서 획득해야함
        ReviewSaveResponseDto reviewSaveResponseDto = reviewService.saveReview(reviewSaveRequestDto, new User());

        return ResponseEntity.ok(
                CommonDto.<ReviewSaveResponseDto>builder()
                        .message(messageUtil.getSuccessMessage())
                        .code(HttpStatus.OK.value())
                        .data(reviewSaveResponseDto)
                        .build()
        );
    }

    @GetMapping("/reviews")
    public ResponseEntity<CommonDto<ReviewGetListDto>> getReviewList(
            @RequestParam("storeId") String storeId,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam("orderby") String orderby,
            @RequestParam("sort") String sort
    ) {
        ReviewGetListDto reviewList = reviewService.getReviewList(storeId, page, size, orderby, sort);

        return ResponseEntity.ok(
                CommonDto.<ReviewGetListDto> builder()
                        .message(messageUtil.getSuccessMessage())
                        .code(HttpStatus.OK.value())
                        .data(reviewList)
                        .build()
        );
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<CommonDto<ReviewDeleteResponseDto>> deleteReview(@PathVariable String reviewId){
        ReviewDeleteResponseDto reviewDeleteResponseDto = reviewService.deleteReview(reviewId, new User());

        return ResponseEntity.ok(
                CommonDto.<ReviewDeleteResponseDto>builder()
                        .message(messageUtil.getSuccessMessage())
                        .code(HttpStatus.OK.value())
                        .data(reviewDeleteResponseDto)
                        .build()
        );
    }

    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<CommonDto<ReviewGetResponseDto>> getReview(@PathVariable String reviewId) {
        ReviewGetResponseDto reviewGetResponseDto = reviewService.getReview(reviewId);

        return ResponseEntity.ok(
            CommonDto.<ReviewGetResponseDto>builder()
                .message(messageUtil.getSuccessMessage())
                .code(HttpStatus.OK.value())
                .data(reviewGetResponseDto)
                .build()
        );
    }
}
