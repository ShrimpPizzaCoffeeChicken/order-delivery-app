package com.fortest.orderdelivery.app.domain.order.repository;

import com.fortest.orderdelivery.app.domain.order.entity.Order;
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
import java.util.Optional;

import static com.fortest.orderdelivery.app.domain.order.entity.QMenuOrder.*;
import static com.fortest.orderdelivery.app.domain.order.entity.QOrder.*;

@RequiredArgsConstructor
@Repository
public class OrderQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    // 사용자의 주문 목록 조회 (검색어 X )
    public Page<Order> findOrderList(Pageable pageable, String userName) {
        // 정렬 기준 변환
        OrderSpecifier<?>[] orderSpecifiers = QueryDslUtil.getAllOrderSpecifierArr(pageable, order);

        List<Order> contents = jpaQueryFactory
                .selectFrom(order)
                .where(
                    order.deletedAt.isNull(),
                    order.customerName.eq(userName)
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
                    order.customerName.eq(userName)
                );

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }

    // 사용자의 주문 목록 조회 (검색어 O )
    public Page<Order> findOrderListUsingSearch(Pageable pageable, String searchKeyword, String userName) {
        // 정렬 기준 변환
        OrderSpecifier<?>[] orderSpecifiers = QueryDslUtil.getAllOrderSpecifierArr(pageable, order);

        List<Order> contents = jpaQueryFactory
                .selectFrom(order)
                .where(
                    order.deletedAt.isNull(),
                    order.storeName.contains(searchKeyword),
                    order.customerName.eq(userName)
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
                    order.storeName.contains(searchKeyword),
                    order.customerName.eq(userName)
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
