package com.fortest.orderdelivery.app.domain.review.controller;

import com.fortest.orderdelivery.app.domain.review.dto.ReviewSaveRequestDto;
import com.fortest.orderdelivery.app.domain.review.dto.ReviewSaveResponseDto;
import com.fortest.orderdelivery.app.domain.review.service.ReviewService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service")
@RequiredArgsConstructor
public class ReviewServiceController {
    private final ReviewService reviewService;

    @PostMapping("/reviews")
    public ResponseEntity<CommonDto<ReviewSaveResponseDto>> saveReview(@RequestBody ReviewSaveRequestDto reviewSaveRequestDto) {
        // TODO : TEMP : userId 를 UserDetail 에서 획득해야함
        ReviewSaveResponseDto reviewSaveResponseDto = reviewService.saveReview(reviewSaveRequestDto, 123L);

        return ResponseEntity.ok(
                CommonDto.<ReviewSaveResponseDto>builder()
                        .message("SUCCESS")
                        .code(HttpStatus.OK.value())
                        .data(reviewSaveResponseDto)
                        .build()
        );
    }

}
