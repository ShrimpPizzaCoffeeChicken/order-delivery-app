package com.fortest.orderdelivery.app.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fortest.orderdelivery.app.domain.order.dto.OrderGetListResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserGetListResponseDto {
    private String search;

    @JsonProperty("total-contents")
    private Long totalContents;

    private Integer size;

    @JsonProperty("current-page")
    private Integer currentPage;

    @JsonProperty("user-list")
    private List<UserDto> userList;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserDto {
        private Long userId;
        private String username;
        private String nickname;
        private String email;
        private String role;
        @JsonProperty("created-at")
        private String createdAt;
        @JsonProperty("created-by")
        private Long createdBy;
        @JsonProperty("updated-at")
        private String updatedAt;
    }
}
