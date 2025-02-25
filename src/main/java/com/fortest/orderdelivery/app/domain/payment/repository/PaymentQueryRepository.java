package com.fortest.orderdelivery.app.domain.payment.repository;

import com.fortest.orderdelivery.app.domain.payment.entity.Payment;
import com.fortest.orderdelivery.app.domain.payment.entity.QPayment;
import com.fortest.orderdelivery.app.domain.payment.entity.QPaymentAgent;
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

import static com.fortest.orderdelivery.app.domain.order.entity.QOrder.order;
import static com.fortest.orderdelivery.app.domain.payment.entity.QPayment.*;
import static com.fortest.orderdelivery.app.domain.payment.entity.QPaymentAgent.*;

@RequiredArgsConstructor
@Repository
public class PaymentQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    // 사용자 주문 목록 조회 (검색어 O )
    public Page<Payment> findPaymentListUsingSearch(Pageable pageable, String userName, String search) {
        OrderSpecifier<?>[] orderSpecifiers = QueryDslUtil.getAllOrderSpecifierArr(pageable, payment);
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        // 키워드가 있으면 결제 번호로 일치조건 조회
        if (!CommonUtil.checkStringIsEmpty(search)) {
            booleanBuilder.and(payment.id.eq(search));
        }

        // 유저 이름이 있으면 해당 유저의 주문 기록만 조회
        if (!CommonUtil.checkStringIsEmpty(userName)) {
            booleanBuilder.and(payment.customerName.eq(userName));
        }

        List<Payment> contents = jpaQueryFactory
                .select(payment)
                .distinct()
                .from(payment)
                .join(payment.paymentAgent, paymentAgent).fetchJoin()
                .where(
                        payment.deletedAt.isNull(),
                        booleanBuilder
                )
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(payment.count())
                .distinct()
                .from(payment)
                .where(
                        payment.deletedAt.isNull(),
                        booleanBuilder
                );

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }
}
