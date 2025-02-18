package com.fortest.orderdelivery.app.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortest.orderdelivery.app.domain.user.dto.LoginRequestDto;
import com.fortest.orderdelivery.app.domain.user.dto.LoginResponseDto;
import com.fortest.orderdelivery.app.domain.user.entity.RoleType;
import com.fortest.orderdelivery.app.domain.user.service.UserService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
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

    private static final String SUCCESS_MESSAGE = "로그인 성공 및 JWT 생성";
    private static final String FAILURE_MESSAGE = "로그인 실패";
    private static final String RESPONSE_TYPE = "application/json";
    private static final String CHARSET_UTF8 = "UTF-8";
    private static final String STATUS_SUCCESS = "success";

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        setFilterProcessesUrl("/api/user/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");
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
        log.info(SUCCESS_MESSAGE);
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        RoleType roleType = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRoleType();

        String accessToken = jwtUtil.createAccessToken(username, roleType.getName());
        String refreshToken = jwtUtil.createRefreshToken(username);

        jwtUtil.addAccessTokenToHeader(accessToken, response);
        jwtUtil.addRefreshTokenToCookie(refreshToken, response);

//        LoginResponseDto responseDto = new LoginResponseDto(accessToken, refreshToken);
//        CommonDto<LoginResponseDto> success = new CommonDto<>(STATUS_SUCCESS, HttpStatus.OK.value(), responseDto);

        CommonDto<String> success = new CommonDto<>(STATUS_SUCCESS, HttpStatus.OK.value(), "로그인 성공");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(success);

        response.setContentType(RESPONSE_TYPE);
        response.setCharacterEncoding(CHARSET_UTF8);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");
        response.setStatus(401);
    }
}
