package com.fortest.orderdelivery.app.domain.review.repository;

import com.fortest.orderdelivery.app.domain.review.entity.Review;
import com.fortest.orderdelivery.app.global.util.QueryDslUtil;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.fortest.orderdelivery.app.domain.review.entity.QReview.review;

@RequiredArgsConstructor
@Repository
public class ReviewQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public Page<Review> findReviewList(String storeId, Pageable pageable) {
        // 정렬 기준 변환
        OrderSpecifier<?>[] orderSpecifiers = QueryDslUtil.getAllOrderSpecifierArr(pageable, review);

        List<Review> contents = jpaQueryFactory
                .select(review)
                .from(review)
                .where(
                    review.deletedAt.isNull(),
                    review.storeId.eq(storeId)
                )
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(review.count())
                .from(review)
                .where(
                    review.deletedAt.isNull(),
                    review.storeId.eq(storeId)
                );

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }
}
