package com.fortest.orderdelivery.app.domain.user.controller;

import com.fortest.orderdelivery.app.global.dto.CommonDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/service/")
@RequiredArgsConstructor
public class ProtectedResourceController {

    @GetMapping("/users/protected-resource")
    public CommonDto<Map<String, Object>> getProtectedResource(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
        System.out.println("컨트롤러");
        if (userDetails == null) {
            return new CommonDto<>("인증 실패: Access Token이 필요합니다.", HttpStatus.UNAUTHORIZED.value(), null);
        }

        // 예제 데이터
        Map<String, Object> data = new HashMap<>();
        data.put("username", userDetails.getUsername());
        data.put("role", userDetails.getAuthorities());

        return new CommonDto<>("정상적으로 데이터를 가져왔습니다.", HttpStatus.OK.value(), data);
    }
}
