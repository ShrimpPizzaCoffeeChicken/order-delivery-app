package com.fortest.orderdelivery.app.domain.menu.repository;

import static com.fortest.orderdelivery.app.domain.image.entity.QImage.image;
import static com.fortest.orderdelivery.app.domain.menu.entity.QMenu.menu;
import static com.fortest.orderdelivery.app.domain.order.entity.QOrder.order;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuListGetResponseDto.MenuListDto;
import com.fortest.orderdelivery.app.domain.menu.entity.Menu;
import com.fortest.orderdelivery.app.global.config.QueryDSLConfig;
import com.fortest.orderdelivery.app.global.util.CommonUtil;
import com.fortest.orderdelivery.app.global.util.QueryDslUtil;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MenuQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Value("${cloud.aws.default.image-url}")
    String defaultImageUrl;

    public Page<MenuListDto> getMenuListPage(Pageable pageable, String storeId) {
        OrderSpecifier<?>[] orderSpecifiers = getAllOrderSpecifiers(pageable, menu);

        List<MenuListDto> contents = queryFactory
            .selectDistinct(Projections.constructor(MenuListDto.class,
                menu.name,
                menu.description,
                menu.price,
                Expressions.stringTemplate("COALESCE({0}, {1})", image.s3Url, defaultImageUrl),
                menu.createdAt,
                menu.updatedAt
                ))
            .from(menu)
            .leftJoin(image).on(image.menu.id.eq(menu.id).and(image.sequence.eq(10)))
            .where(
                menu.storeId.eq(storeId),
                menu.deletedAt.isNull(),
                image.deletedAt.isNull()
            )
            .orderBy(orderSpecifiers)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(menu.countDistinct())
            .from(menu)
            .leftJoin(image).on(image.menu.id.eq(menu.id).and(image.sequence.eq(10)))
            .where(
                menu.storeId.eq(storeId),
                menu.deletedAt.isNull(),
                image.deletedAt.isNull()
            )
            .fetchOne();

        return new PageImpl<>(contents, pageable, total);
    }

    private OrderSpecifier<?>[] getAllOrderSpecifiers(Pageable pageable, Path<?> path) {
        List<OrderSpecifier<?>> orders = QueryDslUtil.getAllOrderSpecifiers(pageable, path);
        return orders.toArray(OrderSpecifier[]::new);
    }
}
