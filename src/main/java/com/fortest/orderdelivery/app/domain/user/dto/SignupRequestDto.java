package com.fortest.orderdelivery.app.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDto {

    private String username;
    private String nickname;
    private String email;
    private String password;
    private String roleId;

}
