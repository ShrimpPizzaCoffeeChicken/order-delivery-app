package com.fortest.orderdelivery.app.domain.order.repository;

import com.fortest.orderdelivery.app.domain.order.entity.Order;
import com.fortest.orderdelivery.app.global.util.CommonUtil;
import com.fortest.orderdelivery.app.global.util.QueryDslUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.fortest.orderdelivery.app.domain.order.entity.QMenuOrder.*;
import static com.fortest.orderdelivery.app.domain.order.entity.QOrder.*;

@RequiredArgsConstructor
@Repository
public class OrderQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    // 사용자의 주문 목록 조회 (검색어 O )
    public Page<Order> findOrderListUsingSearch(Pageable pageable, String searchKeyword, String userName) {
        // 정렬 기준 변환
        OrderSpecifier<?>[] orderSpecifiers = QueryDslUtil.getAllOrderSpecifierArr(pageable, order);

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        // 검색 키워드가 있으면 가게 이름으로 사용
        if (!CommonUtil.checkStringIsEmpty(searchKeyword)) {
            booleanBuilder.and(order.storeName.contains(searchKeyword));
        }
        // 유저 이름이 있으면 해당 유저의 주문 기록만 조회
        if (!CommonUtil.checkStringIsEmpty(userName)) {
            booleanBuilder.and(order.customerName.eq(userName));
        }

        List<Order> contents = jpaQueryFactory
                .selectFrom(order)
                .where(
                    order.deletedAt.isNull(),
                    booleanBuilder
                )
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(order.count())
                .from(order)
                .where(
                    order.deletedAt.isNull(),
                    booleanBuilder
                );

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }

    // 사용자가 요청한 주문 1개에 대한 상세 정보 조회
    public Optional<Order> findOrderDetail (String orderId) {
        return Optional.ofNullable(
                jpaQueryFactory
                    .selectFrom(order)
                    .join(order.menuOrderList, menuOrder)
                    .where(
                            order.id.eq(orderId),
                            order.deletedAt.isNull()
                    )
                    .fetchOne()
        );
    }
}
