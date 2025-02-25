package com.fortest.orderdelivery.app.domain.user.repository;

import com.fortest.orderdelivery.app.domain.user.dto.UserResponseDto;
import com.fortest.orderdelivery.app.domain.user.entity.QUser;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.global.util.CommonUtil;
import com.fortest.orderdelivery.app.global.util.QueryDslUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.fortest.orderdelivery.app.domain.user.entity.QRoleType.*;
import static com.fortest.orderdelivery.app.domain.user.entity.QUser.*;

@RequiredArgsConstructor
@Repository
public class UserQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    public void insertUpdatedAt(User targetUser){
        //updatable = false 우회하여 강제 업데이트
        entityManager.createQuery("UPDATE User u SET u.createdBy = :createdBy WHERE u.id = :id")
                .setParameter("createdBy", targetUser.getId())
                .setParameter("id", targetUser.getId())
                .executeUpdate();
    }

    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(
               jpaQueryFactory
                       .selectFrom(user)
                       .join(user.roleType, roleType).fetchJoin()
                       .where(
                               user.id.eq(userId)
                       )
                       .fetchOne()
        );
    }

    public Optional<User> findUserDetail (Long userId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(user)
                        .where(
                                user.deletedAt.isNull(),
                                user.id.eq(userId)
                        )
                        .fetchOne()
        );
    }

    public Page<User> findUserList(Pageable pageable, String searchKeyword) {

        OrderSpecifier<?>[] orderSpecifiers = QueryDslUtil.getAllOrderSpecifierArr(pageable, user);

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (!CommonUtil.checkStringIsEmpty(searchKeyword)) {
            booleanBuilder.and(user.username.contains(searchKeyword));
        }

        List<User> contents = jpaQueryFactory
                .selectFrom(user)
                .where(
                        booleanBuilder
                )
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(user.count())
                .from(user)
                .where(
                        booleanBuilder
                );

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }

}
