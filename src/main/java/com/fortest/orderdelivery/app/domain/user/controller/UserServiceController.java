package com.fortest.orderdelivery.app.domain.user.controller;

import com.fortest.orderdelivery.app.domain.user.dto.LoginRequestDto;
import com.fortest.orderdelivery.app.domain.user.dto.LoginResponseDto;
import com.fortest.orderdelivery.app.domain.user.dto.SignupRequestDto;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.domain.user.service.UserService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/service/")
@RequiredArgsConstructor
public class UserServiceController {

    private final UserService userService;

    // 토큰 재발급
    @PostMapping("/users/refresh")
    public ResponseEntity<CommonDto<LoginResponseDto>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(userService.refreshToken(request, response));
    }

    // 회원가입
    @PostMapping("/users/signup")
    public ResponseEntity<CommonDto<Void>> signup(@RequestBody SignupRequestDto requestDto) {
        User user = userService.signup(requestDto);
        String successMessage = user.getNickname() + "님의 회원가입이 완료되었습니다.";

        userService.isCreatedBy(user);

        return ResponseEntity.ok(
                CommonDto.<Void>builder()
                        .code(HttpStatus.OK.value())
                        .message(successMessage)
                        .data(null)
                        .build()
        );
    }
}
