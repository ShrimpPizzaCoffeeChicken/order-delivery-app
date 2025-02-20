package com.fortest.orderdelivery.app.domain.menu.repository;

import static com.fortest.orderdelivery.app.domain.image.entity.QImage.image;
import static com.fortest.orderdelivery.app.domain.menu.entity.QMenu.menu;
import static com.fortest.orderdelivery.app.domain.menu.entity.QMenuOption.menuOption;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuGetQueryDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuGetResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuListGetResponseDto.MenuListDto;
import com.fortest.orderdelivery.app.domain.menu.mapper.MenuMapper;
import com.fortest.orderdelivery.app.global.util.QueryDslUtil;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Objects;
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

    public Page<MenuListDto> searchMenuListPage(Pageable pageable, String storeId, String keyword) {
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
                image.deletedAt.isNull(),
                menu.name.contains(keyword)
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
                image.deletedAt.isNull(),
                menu.name.contains(keyword)
            )
            .fetchOne();

        return new PageImpl<>(contents, pageable, total);
    }

    public MenuGetResponseDto getMenuDetails(String menuId) {

        List<String> menuImageList = queryFactory
            .select(Expressions.stringTemplate("COALESCE({0}, {1})", image.s3Url, defaultImageUrl))
            .from(menu)
            .leftJoin(image).on(image.menu.id.eq(menu.id))
            .where(
                menu.id.eq(menuId),
                image.deletedAt.isNull(),
                menu.deletedAt.isNull()
            )
            .orderBy(image.sequence.asc())
            .fetch();

        List<MenuGetResponseDto.OptionList> optionList = queryFactory
            .select(Projections.constructor(MenuGetResponseDto.OptionList.class,
                menuOption.name,
                menuOption.price
            ))
            .from(menu)
            .leftJoin(menuOption).on(menuOption.menu.id.eq(menu.id))
            .where(
                menu.id.eq(menuId),
                menu.deletedAt.isNull(),
                menuOption.deletedAt.isNull()
            )
            .fetch();

        MenuGetQueryDto menuQueryDto = queryFactory
            .select(Projections.fields(MenuGetQueryDto.class,
                menu.name.as("menuName"),
                menu.description.as("menuDescription"),
                menu.price.as("menuPrice")
            ))
            .from(menu)
            .where(
                menu.id.eq(menuId),
                menu.deletedAt.isNull()
            )
            .fetchOne();

        if (!Objects.isNull(menuQueryDto)) {
            return MenuMapper.toMenuGetResponseDto(menuQueryDto, menuImageList, optionList);
        }
        return null;
    }

    private OrderSpecifier<?>[] getAllOrderSpecifiers(Pageable pageable, Path<?> path) {
        List<OrderSpecifier<?>> orders = QueryDslUtil.getAllOrderSpecifiers(pageable, path);
        return orders.toArray(OrderSpecifier[]::new);
    }
}
