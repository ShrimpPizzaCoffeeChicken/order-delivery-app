package com.fortest.orderdelivery.app.domain.category.repository;

import com.fortest.orderdelivery.app.domain.category.entity.Category;
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

import static com.fortest.orderdelivery.app.domain.category.entity.QCategory.category;

@RequiredArgsConstructor
@Repository
public class CategoryQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<Category> findCategoryList(Pageable pageable) {
        // 정렬 기준 변환
        OrderSpecifier<?>[] orderSpecifiers = QueryDslUtil.getAllOrderSpecifierArr(pageable, category);

        List<Category> contents = jpaQueryFactory
                .select(category)
                .from(category)
                .where(
                        category.deletedAt.isNull()
                )
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(category.count())
                .from(category)
                .where(
                        category.deletedAt.isNull()
                );

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }
}
