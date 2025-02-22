package com.fortest.orderdelivery.app.domain.user.controller;

import com.fortest.orderdelivery.app.domain.user.dto.UserResponseDto;
import com.fortest.orderdelivery.app.domain.user.service.UserService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/app")
@RestController
public class UserAppController {

    private final MessageUtil messageUtil;
    private final UserService userService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<CommonDto<UserResponseDto>> getUserData (@PathVariable Long userId) {
        UserResponseDto userData = userService.getUserData(userId);

        return ResponseEntity.ok(
                CommonDto.<UserResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(userData)
                        .build()
        );
    }
}
