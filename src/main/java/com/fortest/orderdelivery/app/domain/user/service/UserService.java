package com.fortest.orderdelivery.app.domain.user.service;

import com.fortest.orderdelivery.app.domain.user.dto.LoginRequestDto;
import com.fortest.orderdelivery.app.domain.user.dto.LoginResponseDto;
import com.fortest.orderdelivery.app.domain.user.dto.SignupRequestDto;
import com.fortest.orderdelivery.app.domain.user.entity.RefreshToken;
import com.fortest.orderdelivery.app.domain.user.entity.RoleType;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.domain.user.repository.RefreshTokenRepository;
import com.fortest.orderdelivery.app.domain.user.repository.RoleTypeRepository;
import com.fortest.orderdelivery.app.domain.user.repository.UserRepository;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleTypeRepository roleTypeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 로그인 관련 기능 (토큰 재발급)
    @Transactional
    public CommonDto<LoginResponseDto> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        log.info("Refresh Token을 이용한 Access Token 재발급 요청 시작");
        String refreshToken = jwtUtil.getRefreshTokenFromCookie(request);
        log.info("가져온 Refresh Token: {}", refreshToken);
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            return new CommonDto<>("Invalid Refresh Token", HttpStatus.UNAUTHORIZED.value(), null);
        }

        //Refresh Token에서 사용자 정보 추출
        Claims claims = jwtUtil.getUserInfoFromToken(refreshToken);
        String username = claims.getSubject();
        log.info("Refresh Token에서 추출한 username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));
        log.info(" 데이터베이스에서 가져온 사용자 정보: {}", user.getUsername());

        //새로운 Access Token 발급
        String newAccessToken = jwtUtil.createAccessToken(username, user.getRoleType().getName());
//        jwtUtil.addAccessTokenToHeader(newAccessToken, response);
        log.info("새롭게 발급된 Access Token: {}", newAccessToken);

        // 새 Access Token을 헤더에 추가
        jwtUtil.addAccessTokenToHeader(newAccessToken, response);
        log.info(" 새 Access Token을 응답 헤더에 추가 완료");

        //새로운 Access Token을 응답으로 반환
        return CommonDto.<LoginResponseDto>builder()
                .message("새로운 Access Token 발급")
                .code(HttpStatus.OK.value())
                .data(new LoginResponseDto(newAccessToken, refreshToken))
                .build();
    }

    // 회원가입 관련 기능
    @Transactional
    public User signup(SignupRequestDto requestDto) {
        RoleType roleType = roleTypeRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new BusinessLogicException("기본 고객 역할을 찾을 수 없습니다."));

        User user = User.builder()
                .username(requestDto.getUsername())
                .nickname(requestDto.getNickname())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .roleType(roleType)
                .build();

        userRepository.save(user);
        return user;
    }

    // 유저 생성자 설정
    @Transactional
    public void isCreatedBy(User user){
        User findUser = userRepository.findById(user.getId()).get();
        findUser.isCreatedBy(findUser.getId());
        userRepository.save(findUser);
    }

    @Transactional(readOnly = true)
    public CommonDto<Map<String, Object>> checkUsernameAvailability(String username) {
        //존재하는 아이디이면 false, 존재하지 않으면 true
        boolean isAvailable = !userRepository.existsByUsername(username);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("username", username);
        responseData.put("is-available", isAvailable);

        String message = isAvailable ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다.";
        HttpStatus status = isAvailable ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        //응답 객체 생성 및 반환
        return new CommonDto<>(message, status.value(), responseData);
    }

    @Transactional
    public CommonDto<String> logout(HttpServletRequest request, HttpServletResponse response) {
        // 현재 쿠키에서 Refresh Token 가져오기
        String refreshToken = jwtUtil.getRefreshTokenFromCookie(request);

        if (refreshToken == null) {
            return new CommonDto<>("Refresh Token이 존재하지 않습니다.", HttpStatus.BAD_REQUEST.value(), null);
        }

        // Refresh Token 삭제 (쿠키에서 제거)
        removeRefreshTokenCookie(response);

        return new CommonDto<>("로그아웃 완료", HttpStatus.OK.value(), "로그아웃 성공");
    }

    private void removeRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(JwtUtil.REFRESH_TOKEN_COOKIE, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키 만료 설정

        response.addCookie(cookie);
    }

}
