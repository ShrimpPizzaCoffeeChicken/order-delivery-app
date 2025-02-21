package com.fortest.orderdelivery.app.domain.user.repository;

import com.fortest.orderdelivery.app.domain.user.dto.UserResponseDto;
import com.fortest.orderdelivery.app.domain.user.entity.QRoleType;
import com.fortest.orderdelivery.app.domain.user.entity.QUser;
import com.fortest.orderdelivery.app.domain.user.entity.RoleType;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.fortest.orderdelivery.app.domain.user.entity.QRoleType.*;
import static com.fortest.orderdelivery.app.domain.user.entity.QUser.user;

@RequiredArgsConstructor
@Repository
public class UserQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QUser user = QUser.user;

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

    public List<UserResponseDto> findUsersByFilters(String username, String nickname, String roleName) {
        BooleanBuilder builder = new BooleanBuilder();

        if (username != null) {
            builder.and(user.username.eq(username));
        }
        if (nickname != null) {
            builder.and(user.nickname.eq(nickname));
        }
        if (roleName != null) {
            builder.and(user.roleType.name.eq(roleName));
        }

        List<User> users = jpaQueryFactory
                .selectFrom(user)
                .join(user.roleType, roleType)
                .where(builder)
                .fetch();

        return users.stream()
                .map(u -> UserResponseDto.builder()
                        .userId(u.getId())
                        .username(u.getUsername())
                        .nickname(u.getNickname())
                        .email(u.getEmail())
                        .role(u.getRoleType().getName())
                        .isPublic(u.getIsPublic())
                        .createdAt(u.getCreatedAt() != null ? u.getCreatedAt().toString() : null)
                        .createdBy(u.getCreatedBy())
                        .updatedAt(u.getUpdatedAt() != null ? u.getUpdatedAt().toString() : null)
                        .updatedBy(u.getUpdatedBy())
                        .build())
                .collect(Collectors.toList());
    }

}
