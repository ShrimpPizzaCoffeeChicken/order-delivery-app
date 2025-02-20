package com.fortest.orderdelivery.app.domain.store.repository;

import com.fortest.orderdelivery.app.domain.area.entity.QArea;
import com.fortest.orderdelivery.app.domain.store.entity.Store;
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

import static com.fortest.orderdelivery.app.domain.area.entity.QArea.*;
import static com.fortest.orderdelivery.app.domain.category.entity.QCategory.*;
import static com.fortest.orderdelivery.app.domain.category.entity.QCategoryStore.*;
import static com.fortest.orderdelivery.app.domain.store.entity.QStore.store;


@RequiredArgsConstructor
@Repository
public class StoreQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 가게 목록 검색
     * @param pageable
     * @param categoryId (nullable)
     * @param city (nullable)
     * @param district (nullable)
     * @param street (nullable)
     * @param search (nullable) : 가게 이름 키워드 (포함조건)
     */
    public Page<Store> findStoreList (Pageable pageable, String categoryId, String city, String district, String street, String search) {
        // 정렬 기준 변환
        OrderSpecifier<?>[] orderSpecifiers = QueryDslUtil.getAllOrderSpecifierArr(pageable, store);

        List<Store> contents = jpaQueryFactory
                .selectFrom(store)
                .distinct()
                .join(store.area, area).fetchJoin()
                .leftJoin(store.categoryStoreList, categoryStore)
                .leftJoin(categoryStore.category, category)
                .where(
                        store.deletedAt.isNull(),
                        getWhereForSearch(categoryId, city, district, street, search)
                )
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(store.countDistinct())
                .from(store)
                .join(store.area, area)
                .leftJoin(store.categoryStoreList, categoryStore)
                .leftJoin(categoryStore.category, category)
                .where(
                        store.deletedAt.isNull(),
                        getWhereForSearch(categoryId, city, district, street, search)
                );

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }

    /**
     * 검색 조건에 의한 where 절 동적 생성
     * categoryId 는 값이 있는 경우에만 검색 조건에 포함
     * search 키워드는 가게 이름에 포함조건으로 사용
     * 지역조건 city -> district -> street 순으로 동적 생성
     * ex : city, district 값만 존재하고 street 은 존재하지 않을 경우 district 까지만 생성
     * ex : city, street 값 존재하고 district 은 존재하지 않을 경우 city 까지만 생성
     * @param city
     * @param district
     * @param street
     * @return
     */
    private BooleanBuilder getWhereForSearch(String categoryId, String city, String district, String street, String search) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (!CommonUtil.checkStringIsEmpty(categoryId)) {
            booleanBuilder.and(categoryStore.category.id.eq(categoryId));
        }

        if (!CommonUtil.checkStringIsEmpty(search)) {
            booleanBuilder.and(store.name.contains(search));
        }

        if (CommonUtil.checkStringIsEmpty(city)) {
            return booleanBuilder;
        }
        booleanBuilder.and(store.area.city.eq(city));

        if (CommonUtil.checkStringIsEmpty(district)) {
            return booleanBuilder;
        }
        booleanBuilder.and(store.area.district.eq(district));

        if (CommonUtil.checkStringIsEmpty(street)) {
            return booleanBuilder;
        }
        booleanBuilder.and(store.area.street.eq(street));

        return booleanBuilder;
    }

    public Optional<Store> findStoreDetail (String storeId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(store)
                        .where(
                                store.deletedAt.isNull(),
                                store.id.eq(storeId)
                        )
                        .fetchOne()
        );
    }
}
