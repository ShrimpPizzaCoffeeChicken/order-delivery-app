package com.fortest.orderdelivery.app.domain.payment.repository;

import com.fortest.orderdelivery.app.domain.payment.entity.Payment;
import com.fortest.orderdelivery.app.domain.payment.entity.QPayment;
import com.fortest.orderdelivery.app.domain.payment.entity.QPaymentAgent;
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

import static com.fortest.orderdelivery.app.domain.payment.entity.QPayment.*;
import static com.fortest.orderdelivery.app.domain.payment.entity.QPaymentAgent.*;

@RequiredArgsConstructor
@Repository
public class PaymentQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    // 사용자 주문 목록 조회 (검색어 X)
    public Page<Payment> findPaymentList(Pageable pageable, String userName) {
        OrderSpecifier<?>[] orderSpecifiers = QueryDslUtil.getAllOrderSpecifierArr(pageable, payment);

        List<Payment> contents = jpaQueryFactory
                .select(payment)
                .distinct()
                .from(payment)
                .where(
                        payment.deletedAt.isNull(),
                        payment.customerName.eq(userName)
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
                        payment.customerName.eq(userName)
                );

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }

    // 사용자 주문 목록 조회 (검색어 O )
    public Page<Payment> findPaymentListUsingSearch(Pageable pageable, String userName, String search) {
        OrderSpecifier<?>[] orderSpecifiers = QueryDslUtil.getAllOrderSpecifierArr(pageable, payment);

        List<Payment> contents = jpaQueryFactory
                .select(payment)
                .distinct()
                .from(payment)
                .join(payment.paymentAgent, paymentAgent).fetchJoin()
                .where(
                        payment.deletedAt.isNull(),
                        payment.customerName.eq(userName),
                        payment.id.eq(search)
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
                        payment.customerName.eq(userName),
                        payment.id.eq(search)
                );

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }
}
