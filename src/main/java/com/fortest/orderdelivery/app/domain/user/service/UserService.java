package com.fortest.orderdelivery.app.domain.user.service;

import com.fortest.orderdelivery.app.domain.user.dto.LoginRequestDto;
import com.fortest.orderdelivery.app.domain.user.dto.LoginResponseDto;
import com.fortest.orderdelivery.app.domain.user.entity.RefreshToken;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.domain.user.repository.RefreshTokenRepository;
import com.fortest.orderdelivery.app.domain.user.repository.UserRepository;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
//    private final RefreshTokenRepository refreshTokenRepository;
//    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
//    private final AuthenticationManager authenticationManager;


//    @Transactional
//    public CommonDto<LoginResponseDto> login(LoginRequestDto requestDto, HttpServletResponse response) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword())
//        );
//
//        log.info("로그인 서비스 진입");
//        User user = userRepository.findByUsername(requestDto.getUsername())
//                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
//
//        // Access Token & Refresh Token 생성
//        String accessToken = jwtUtil.createAccessToken(user.getUsername(), user.getRoleType().getName());
//        String refreshToken = jwtUtil.createRefreshToken(user.getUsername());
//
//        // 기존 Refresh Token 삭제 후 새로운 Token 저장
//        refreshTokenRepository.deleteByUsername(user.getUsername());
//        RefreshToken refreshTokenEntity = new RefreshToken(user.getUsername(), refreshToken);
//        refreshTokenRepository.save(refreshTokenEntity);
//
//        // Access Token은 Header에, Refresh Token은 Cookie에 추가
//        jwtUtil.addAccessTokenToHeader(accessToken, response);
//        jwtUtil.addRefreshTokenToCookie(refreshToken, response);
//
//        return CommonDto.<LoginResponseDto>builder()
//                .message("로그인 성공")
//                .code(200)
//                .data(new LoginResponseDto(accessToken, refreshToken))
//                .build();
//    }



    @Transactional
    public CommonDto<LoginResponseDto> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtil.getRefreshTokenFromCookie(request);

        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 Refresh Token입니다.");
        }

        Claims claims = jwtUtil.getUserInfoFromToken(refreshToken);
        String username = claims.getSubject();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        String newAccessToken = jwtUtil.createAccessToken(username, user.getRoleType().getName());
        jwtUtil.addAccessTokenToHeader(newAccessToken, response);

        return CommonDto.<LoginResponseDto>builder()
                .message("새로운 Access Token 발급")
                .code(200)
                .data(new LoginResponseDto(newAccessToken, refreshToken))
                .build();
    }

}
