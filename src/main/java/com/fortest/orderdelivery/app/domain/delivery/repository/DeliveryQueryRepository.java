package com.fortest.orderdelivery.app.domain.delivery.repository;

import com.fortest.orderdelivery.app.domain.delivery.entity.Delivery;
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

import static com.fortest.orderdelivery.app.domain.delivery.entity.QDelivery.*;

@RequiredArgsConstructor
@Repository
public class DeliveryQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    // 사용자의 베송 목록 조회 (검색어 X )
    public Page<Delivery> findDeliveryList(Pageable pageable, String userName) {
        // 정렬 기준 변환
        OrderSpecifier<?>[] orderSpecifiers = QueryDslUtil.getAllOrderSpecifierArr(pageable, delivery);

        List<Delivery> contents = jpaQueryFactory
                .select(delivery)
                .from(delivery)
                .where(
                        delivery.deletedAt.isNull(),
                        delivery.customerName.eq(userName)
                )
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(delivery.count())
                .from(delivery)
                .where(
                        delivery.deletedAt.isNull(),
                        delivery.customerName.eq(userName)
                );

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }

    // 사용자의 배송 목록 조회 (검색어 O )
    public Page<Delivery> findDeliveryListUsingSearch(Pageable pageable, String searchKeyword, String userName) {
        // 정렬 기준 변환
        OrderSpecifier<?>[] orderSpecifiers = QueryDslUtil.getAllOrderSpecifierArr(pageable, delivery);

        List<Delivery> contents = jpaQueryFactory
                .select(delivery)
                .from(delivery)
                .where(
                        delivery.deletedAt.isNull(),
                        delivery.customerName.eq(userName),
                        delivery.status.eq(Delivery.Status.valueOf(searchKeyword))
                )
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(delivery.count())
                .from(delivery)
                .where(
                        delivery.deletedAt.isNull(),
                        delivery.customerName.eq(userName),
                        delivery.status.eq(Delivery.Status.valueOf(searchKeyword))
                );

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }

    public Optional<Delivery> findDeliveryDetail (String deliveryId, String userName) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(delivery)
                        .where(
                                delivery.deletedAt.isNull(),
                                delivery.customerName.eq(userName)
                        )
                        .fetchOne()
        );
    }
}
