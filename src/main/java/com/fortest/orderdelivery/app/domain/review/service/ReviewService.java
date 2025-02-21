package com.fortest.orderdelivery.app.domain.review.service;

import com.fortest.orderdelivery.app.domain.category.dto.UserResponseDto;
import com.fortest.orderdelivery.app.domain.review.dto.OrderDetailsResponseDto;
import com.fortest.orderdelivery.app.domain.review.dto.ReviewDeleteResponseDto;
import com.fortest.orderdelivery.app.domain.review.dto.ReviewGetListDto;
import com.fortest.orderdelivery.app.domain.review.dto.ReviewGetResponseDto;
import com.fortest.orderdelivery.app.domain.review.dto.ReviewSaveRequestDto;
import com.fortest.orderdelivery.app.domain.review.dto.ReviewSaveResponseDto;
import com.fortest.orderdelivery.app.domain.review.entity.Review;
import com.fortest.orderdelivery.app.domain.review.mapper.ReviewMapper;
import com.fortest.orderdelivery.app.domain.review.repository.ReviewQueryRepository;
import com.fortest.orderdelivery.app.domain.review.repository.ReviewRepository;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.util.JpaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewQueryRepository reviewQueryRepository;
    private final MessageSource messageSource;

    private static final String ORDER_APP_URL = "http://{url}:{port}/api/app/orders/{orderId}/details";

    @Transactional
    public ReviewSaveResponseDto saveReview(ReviewSaveRequestDto reviewSaveRequestDto, Long userId) {
        // TODO : 유저 검색
        CommonDto<UserResponseDto> validUserResponse = getUserId(userId); // api 요청
        if (validUserResponse == null || validUserResponse.getData() == null) {
            throw new BusinessLogicException(messageSource.getMessage("api.call.client-error", null, Locale.KOREA));
        }
        throwByRespCode(validUserResponse.getCode());
        String username = validUserResponse.getData().getUsername();

        Review newReview = ReviewMapper.toReview(reviewSaveRequestDto);
        Review savedReview = reviewRepository.save(newReview);

        return ReviewMapper.toReviewSaveResponseDto(savedReview);
    }
    // TODO : 유저 조회 : 하단 코드로 교체 예정
    public CommonDto<UserResponseDto> getUserId(Long id) {
        UserResponseDto userDto = UserResponseDto.builder()
                .id(id)
                .build();

        return new CommonDto<>("SUCCESS", HttpStatus.OK.value(), userDto);
    }

//    public CommonDto<UserResponseDto> getUserId(Long id) {
//        String targetUrl = USER_APP_URL
//                .replace("{host}", "localhost")
//                .replace("{port}", "8082")
//                .replace("{userId}", id);
//
//        return webClient.get()
//                .uri(targetUrl)
//                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .retrieve()
//                .bodyToMono(new ParameterizedTypeReference<CommonDto<UserResponseDto>>() {})
//                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) // 에러 발생 시 2초 간격으로 최대 3회 재시작
//                .onErrorResume(throwable -> {
//                    log.error("Fail : {}", targetUrl, throwable);
//                    return Mono.empty();
//                })
//                .block();
//    }

    public ReviewGetListDto getReviewList(String storeId, Integer page, Integer size, String orderby, String sort) {

        PageRequest pageable = JpaUtil.getNormalPageable(page, size, orderby, sort);
        Page<Review> reviewList;

        reviewList = reviewQueryRepository.findReviewList(storeId, pageable);
        return ReviewMapper.pageToGetReviewListDto(reviewList);
    }

    @Transactional
    public ReviewDeleteResponseDto deleteReview(String reviewId, Long userId){
        Review review = reviewRepository.findById(reviewId).orElseThrow(()->
                new BusinessLogicException(messageSource.getMessage("api.call.client-error",null, Locale.KOREA)));

        review.isDeletedNow(userId);

        return ReviewMapper.toReviewDeleteResponseDto(review);
    }

    private void throwByRespCode(int httpStatusCode) {
        int firstNum = httpStatusCode / 100;
        switch (firstNum) {
            case 4 -> {
                throw new BusinessLogicException(messageSource.getMessage("api.call.client-error", null, Locale.KOREA));
            }
            case 5 -> {
                throw new BusinessLogicException(messageSource.getMessage("api.call.server-error", null, Locale.KOREA));
            }
        }
    }

    public ReviewGetResponseDto getReview(String reviewId) {
        // TODO : 주문 상세 조회 API 생성 후 테스트
//        CommonDto<OrderDetailsResponseDto> commonDto = getOrderDetailsFromApp(review.getOrderId());
//        if(Objects.isNull(commonDto) || Objects.isNull(commonDto.getData())) {
//            throw new BusinessLogicException("Order is not Valid");
//        }
        Review review = reviewRepository.findById(reviewId).orElseThrow(()->
            new BusinessLogicException(messageSource.getMessage("api.call.client-error",null, Locale.KOREA)));

        return ReviewMapper.toReviewGetResponseDto(review, OrderDetailsResponseDto.builder().build());
    }

    /**
     * 주문 서비스에 메뉴리스트, 옵션리스트 요청
     *
     * @param orderId
     * @return CommonDto<OrderDetailsResponseDto> : 요청 실패 시 null
     */
//    public CommonDto<OrderDetailsResponseDto> getOrderDetailsFromApp(String orderId) {
//
//        String targetUrl = ORDER_APP_URL
//            .replace("{url}", "localhost")
//            .replace("{port}", "8082")
//            .replace("{orderId}", orderId);
//
//        return webClient.get()
//            .uri(targetUrl)
//            .retrieve()
//            .bodyToMono(new ParameterizedTypeReference<CommonDto<OrderDetailsResponseDto>>() {
//            })
//            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))) //에러 발생 시 2초 간격으로 최대 3회 재시도
//            .onErrorResume(throwable -> {
//                log.error("Fail : {}", targetUrl, throwable);
//                return Mono.empty();
//            })
//            .block();
//    }
}
