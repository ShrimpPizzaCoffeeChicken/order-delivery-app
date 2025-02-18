package com.fortest.orderdelivery.app.domain.order.repository;

import com.fortest.orderdelivery.app.domain.order.entity.Order;
import com.fortest.orderdelivery.app.domain.order.entity.QOrder;
import com.fortest.orderdelivery.app.global.util.QueryDslUtil;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.fortest.orderdelivery.app.domain.order.entity.QOrder.*;

@RequiredArgsConstructor
@Repository
public class OrderQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<Order> findOrderList(Pageable pageable, String userName) {
        // 정렬 기준 변환
        OrderSpecifier<?>[] orderSpecifiers = getAllOrderSpecifiers(pageable, order);

        List<Order> content = jpaQueryFactory
                .select(order)
                .distinct()
                .from(order)
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
                .distinct()
                .from(order)
                .where(
                    order.deletedAt.isNull(),
                    order.customerName.eq(userName)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    public Page<Order> findOrderListUsingSearch(Pageable pageable, String searchKeyword, String userName) {
        // 정렬 기준 변환
        OrderSpecifier<?>[] orderSpecifiers = getAllOrderSpecifiers(pageable, order);

        List<Order> content = jpaQueryFactory
                .select(order)
                .distinct()
                .from(order)
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
                .distinct()
                .from(order)
                .where(
                    order.deletedAt.isNull(),
                    order.storeName.contains(searchKeyword),
                    order.customerName.eq(userName)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private OrderSpecifier<?>[] getAllOrderSpecifiers(Pageable pageable, Path<?> path) {
        List<OrderSpecifier<?>> orders = QueryDslUtil.getAllOrderSpecifiers(pageable, path);
        return orders.toArray(OrderSpecifier[]::new);
    }
}
