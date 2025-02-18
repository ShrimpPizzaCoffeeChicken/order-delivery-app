package com.fortest.orderdelivery.app.domain.category.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto  {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String password;

    @JsonProperty("is-public")
    private boolean isPublic;

    @JsonProperty("created-at")
    private String createdAt;
    @JsonProperty("created-by")
    private String createdBy;
    @JsonProperty("updated-at")
    private String updatedAt;
    @JsonProperty("updated-by")
    private String updatedBy;
}