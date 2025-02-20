package com.fortest.orderdelivery.app.domain.ai.repository;

import com.fortest.orderdelivery.app.domain.ai.entity.AiRequest;
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

import static com.fortest.orderdelivery.app.domain.ai.entity.QAiRequest.*;

@RequiredArgsConstructor
@Repository
public class AiRequestQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    // 특정 가게의 AI 질문 목록 조회 (검색어 X )
    public Page<AiRequest> findAiRequestList(Pageable pageable, String storeId) {
        // 정렬 기준 변환
        OrderSpecifier<?>[] orderSpecifiers = QueryDslUtil.getAllOrderSpecifierArr(pageable, aiRequest);

        List<AiRequest> contents = jpaQueryFactory
                .selectFrom(aiRequest)
                .where(
                        aiRequest.deletedAt.isNull(),
                        aiRequest.storeId.eq(storeId)
                )
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(aiRequest.count())
                .from(aiRequest)
                .where(
                        aiRequest.deletedAt.isNull(),
                        aiRequest.storeId.eq(storeId)
                );
        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }

    // 특정 가게의 AI 질문 목록 조회 (검색어 O )
    public Page<AiRequest> findAiRequestListUsingSearch(Pageable pageable, String storeId, String search) {
        // 정렬 기준 변환
        OrderSpecifier<?>[] orderSpecifiers = QueryDslUtil.getAllOrderSpecifierArr(pageable, aiRequest);

        List<AiRequest> contents = jpaQueryFactory
                .selectFrom(aiRequest)
                .where(
                        aiRequest.deletedAt.isNull(),
                        aiRequest.storeId.eq(storeId),
                        aiRequest.question.contains(search)
                )
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(aiRequest.count())
                .from(aiRequest)
                .where(
                        aiRequest.deletedAt.isNull(),
                        aiRequest.storeId.eq(storeId),
                        aiRequest.question.contains(search)
                );
        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }
}
