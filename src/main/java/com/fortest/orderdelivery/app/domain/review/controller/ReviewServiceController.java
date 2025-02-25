package com.fortest.orderdelivery.app.domain.review.controller;

import com.fortest.orderdelivery.app.domain.review.dto.*;
import com.fortest.orderdelivery.app.domain.review.service.ReviewService;
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
public class ReviewServiceController {

    private final MessageUtil messageUtil;
    private final ReviewService reviewService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/reviews")
    public ResponseEntity<CommonDto<ReviewSaveResponseDto>> saveReview(@Valid @RequestBody ReviewSaveRequestDto reviewSaveRequestDto,
                                                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {

        ReviewSaveResponseDto reviewSaveResponseDto = reviewService.saveReview(reviewSaveRequestDto, userDetails.getUser());

        return ResponseEntity.ok(
                CommonDto.<ReviewSaveResponseDto>builder()
                        .message(messageUtil.getSuccessMessage())
                        .code(HttpStatus.OK.value())
                        .data(reviewSaveResponseDto)
                        .build()
        );
    }

    @GetMapping("/reviews")
    public ResponseEntity<CommonDto<ReviewGetListResponseDto>> getReviewList(
            @Valid ReviewGetListRequestDto requestDto
    ) {
        ReviewGetListResponseDto reviewList = reviewService.getReviewList(
                requestDto.getStoreId(),
                requestDto.getPage(),
                requestDto.getSize(),
                requestDto.getOrderby(),
                requestDto.getSort()
        );

        return ResponseEntity.ok(
                CommonDto.<ReviewGetListResponseDto> builder()
                        .message(messageUtil.getSuccessMessage())
                        .code(HttpStatus.OK.value())
                        .data(reviewList)
                        .build()
        );
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<CommonDto<ReviewDeleteResponseDto>> deleteReview(@PathVariable String reviewId,
                                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ReviewDeleteResponseDto reviewDeleteResponseDto = reviewService.deleteReview(reviewId, userDetails.getUser());

        return ResponseEntity.ok(
                CommonDto.<ReviewDeleteResponseDto>builder()
                        .message(messageUtil.getSuccessMessage())
                        .code(HttpStatus.OK.value())
                        .data(reviewDeleteResponseDto)
                        .build()
        );
    }

    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<CommonDto<ReviewGetResponseDto>> getReview(@PathVariable("reviewId") String reviewId) {
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
