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

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleTypeRepository roleTypeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 🔹 로그인 관련 기능 (토큰 재발급)
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
                .code(HttpStatus.OK.value())
                .data(new LoginResponseDto(newAccessToken, refreshToken))
                .build();
    }

    // 🔹 회원가입 관련 기능
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

    // 🔹 유저 생성자 설정
    @Transactional
    public void isCreatedBy(User user) {
        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new BusinessLogicException("사용자를 찾을 수 없습니다."));
        findUser.isCreatedBy(findUser.getId());
        userRepository.save(findUser);
    }
}
