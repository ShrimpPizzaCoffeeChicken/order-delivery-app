package com.fortest.orderdelivery.app.domain.delivery.repository;

import com.fortest.orderdelivery.app.domain.delivery.entity.Delivery;
import com.fortest.orderdelivery.app.global.util.CommonUtil;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
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

import static com.fortest.orderdelivery.app.domain.delivery.entity.QDelivery.*;
import static com.fortest.orderdelivery.app.domain.order.entity.QOrder.order;

@RequiredArgsConstructor
@Repository
public class DeliveryQueryRepository {

    private final MessageUtil messageUtil;
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

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        // 검색 키워드가 있으면 배달의 상태 일치 조건으로 검색
        if (!CommonUtil.checkStringIsEmpty(searchKeyword)) {
            booleanBuilder.and(delivery.status.eq(Delivery.Status.getByString(messageUtil, searchKeyword)));
        }
        // 유저 이름이 있으면 해당 유저의 주문 기록만 조회
        if (!CommonUtil.checkStringIsEmpty(userName)) {
            booleanBuilder.and(delivery.customerName.eq(userName));
        }

        List<Delivery> contents = jpaQueryFactory
                .select(delivery)
                .from(delivery)
                .where(
                        delivery.deletedAt.isNull(),
                        booleanBuilder
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
                        booleanBuilder
                );

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }

    public Optional<Delivery> findDeliveryDetail (String deliveryId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(delivery)
                        .where(
                                delivery.id.eq(deliveryId),
                                delivery.deletedAt.isNull()
                        )
                        .fetchOne()
        );
    }
}
