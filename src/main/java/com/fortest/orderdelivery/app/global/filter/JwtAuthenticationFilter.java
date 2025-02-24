package com.fortest.orderdelivery.app.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortest.orderdelivery.app.domain.user.dto.LoginRequestDto;
import com.fortest.orderdelivery.app.domain.user.dto.LoginResponseDto;
import com.fortest.orderdelivery.app.domain.user.entity.RoleType;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.domain.user.service.UserService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.jwt.JwtUtil;
import com.fortest.orderdelivery.app.global.security.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final UserService userService;

    private static final String FAILURE_MESSAGE = "로그인 실패";
    private static final String RESPONSE_TYPE = "application/json";
    private static final String CHARSET_UTF8 = "UTF-8";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_DELETE = "삭제된 계정입니다.";

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        setFilterProcessesUrl("/api/service/users/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUsername(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        UserDetailsImpl principal = (UserDetailsImpl) authResult.getPrincipal();
        User user = principal.getUser();

        if (user.getDeletedAt() != null || user.getDeletedBy() != null) {
            CommonDto<String> failureResponse = new CommonDto<>(
                    FAILURE_MESSAGE, HttpStatus.UNAUTHORIZED.value(), STATUS_DELETE);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(failureResponse);

            response.setContentType(RESPONSE_TYPE);
            response.setCharacterEncoding(CHARSET_UTF8);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(jsonResponse);
            response.getWriter().flush();
            return;
        }

        String username = user.getUsername();
        RoleType roleType = user.getRoleType();

        String accessToken = jwtUtil.createAccessToken(user.getId() ,username, roleType.getRoleName().name());
        String refreshToken = jwtUtil.createRefreshToken(user.getId(),username);

        jwtUtil.addAccessTokenToHeader(accessToken, response);
        jwtUtil.addRefreshTokenToCookie(refreshToken, response);

        CommonDto<LoginResponseDto> success = new CommonDto<>(
                STATUS_SUCCESS, HttpStatus.OK.value(),null);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(success);

        response.setContentType(RESPONSE_TYPE);
        response.setCharacterEncoding(CHARSET_UTF8);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        CommonDto<String> failureResponse = new CommonDto<>(
                FAILURE_MESSAGE, HttpStatus.UNAUTHORIZED.value(), null);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(failureResponse);

        response.setContentType(RESPONSE_TYPE);
        response.setCharacterEncoding(CHARSET_UTF8);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
