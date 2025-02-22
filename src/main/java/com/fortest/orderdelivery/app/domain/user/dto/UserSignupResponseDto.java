package com.fortest.orderdelivery.app.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSignupResponseDto {

    private Long id;
    private String username;
    private String nickname;
    private String email;

}
