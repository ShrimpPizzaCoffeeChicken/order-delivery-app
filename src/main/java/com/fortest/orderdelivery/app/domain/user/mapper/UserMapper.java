package com.fortest.orderdelivery.app.domain.user.mapper;

import com.fortest.orderdelivery.app.domain.order.dto.OrderGetListResponseDto;
import com.fortest.orderdelivery.app.domain.order.entity.Order;
import com.fortest.orderdelivery.app.domain.order.mapper.OrderMapper;
import com.fortest.orderdelivery.app.domain.user.dto.UserGetDetailResponseDto;
import com.fortest.orderdelivery.app.domain.user.dto.UserGetListResponseDto;
import com.fortest.orderdelivery.app.domain.user.dto.UserResponseDto;
import com.fortest.orderdelivery.app.domain.user.dto.UserSignupResponseDto;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.global.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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

    public static UserSignupResponseDto fromUserToUserSignupResponseDto(User user) {
        return UserSignupResponseDto.builder()
            //    .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();
    }

    public static UserGetListResponseDto pageToGetUserListDto(Page<User> page, String search) {
        UserGetListResponseDto.UserGetListResponseDtoBuilder builder = UserGetListResponseDto.builder();
        builder = builder
                .search(search == null ? "" : search)
                .totalContents(page.getTotalElements())
                .size(page.getSize())
                .currentPage(page.getNumber() + 1);
        List<UserGetListResponseDto.UserDto> userDtoList = page.getContent().stream()
                .map(UserMapper::entityToUserListDtoElement)
                .collect(Collectors.toList());
        builder = builder.userList(userDtoList);
        return builder.build();
    }

    private static UserGetListResponseDto.UserDto entityToUserListDtoElement(final User user) {
        return UserGetListResponseDto.UserDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRoleType().getRoleName().name())
                .email(user.getEmail())
                .createdAt(CommonUtil.LDTToString(user.getCreatedAt()))
                .createdBy(user.getCreatedBy())
                .updatedAt(CommonUtil.LDTToString(user.getUpdatedAt()))
                .build();
    }

}
