package com.fortest.orderdelivery.app.domain.user.mapper;

import com.fortest.orderdelivery.app.domain.user.dto.UserGetDetailResponseDto;
import com.fortest.orderdelivery.app.domain.user.dto.UserResponseDto;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.global.util.CommonUtil;

public class UserMapper {

    public static UserResponseDto entityToUserResponseDto (User user) {
        return UserResponseDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .role(user.getRoleType().getRoleName().name())
                .isPublic(user.getIsPublic())
                .createdAt(CommonUtil.LDTToString(user.getCreatedAt()))
                .createdBy(user.getCreatedBy())
                .updatedAt(CommonUtil.LDTToString(user.getUpdatedAt()))
                .updatedBy(user.getUpdatedBy())
                .build();
    }

    public static UserGetDetailResponseDto toUserGetDetailResponseDto(User user) {
        return UserGetDetailResponseDto.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .role(user.getRoleType().getRoleName().name())
                .isPublic(user.getIsPublic())
                .createdAt(CommonUtil.LDTToString(user.getCreatedAt()))
                .createdBy(user.getCreatedBy())
                .updatedAt(CommonUtil.LDTToString(user.getUpdatedAt()))
                .updatedBy(user.getUpdatedBy())
                .build();
    }
}
