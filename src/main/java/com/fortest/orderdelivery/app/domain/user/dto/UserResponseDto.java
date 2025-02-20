package com.fortest.orderdelivery.app.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

        private Long userId;
        private String username;
        private String nickname;
        private String email;
        private String role;

        @JsonProperty("is-public")
        private Boolean isPublic;

        @JsonProperty("created-at")
        private String createdAt;
        @JsonProperty("created-by")
        private Long createdBy;
        @JsonProperty("updated-at")
        private String updatedAt;
        @JsonProperty("updated-by")
        private Long updatedBy;
}
