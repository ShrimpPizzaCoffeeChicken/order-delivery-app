package com.fortest.orderdelivery.app.domain.user.controller;

import com.fortest.orderdelivery.app.domain.user.service.UserService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service/")
@RequiredArgsConstructor
public class LogoutController {

    private final UserService userService;

    @PostMapping("/users/logout")
    public CommonDto<String> logout(HttpServletRequest request, HttpServletResponse response) {
        return userService.logout(request, response);
    }
}
