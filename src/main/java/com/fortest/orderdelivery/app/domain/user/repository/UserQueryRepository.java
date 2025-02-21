package com.fortest.orderdelivery.app.domain.user.repository;

import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.fortest.orderdelivery.app.domain.user.entity.QUser.user;

@RequiredArgsConstructor
@Repository
public class UserQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

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
}
