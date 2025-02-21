package com.fortest.orderdelivery.app.domain.user.service;

import com.fortest.orderdelivery.app.domain.user.dto.*;
import com.fortest.orderdelivery.app.domain.user.entity.RoleType;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.domain.user.mapper.UserMapper;
import com.fortest.orderdelivery.app.domain.user.repository.RoleTypeRepository;
import com.fortest.orderdelivery.app.domain.user.repository.UserRepository;
import com.fortest.orderdelivery.app.domain.user.repository.UserQueryRepository;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.exception.NotFoundException;
import com.fortest.orderdelivery.app.global.exception.NotValidRequestException;
import com.fortest.orderdelivery.app.global.jwt.JwtUtil;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final MessageUtil messageUtil;
    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final RoleTypeRepository roleTypeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserResponseDto getUserData(Long findTargetUserId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(findTargetUserId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.user")));

        return UserMapper.entityToUserResponseDto(user);
    }

    // 로그인 관련 기능 (토큰 재발급)
    @Transactional
    public CommonDto<LoginResponseDto> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtil.getRefreshTokenFromCookie(request);
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            return new CommonDto<>("Invalid Refresh Token", HttpStatus.UNAUTHORIZED.value(), null);
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

    @Transactional
    public UserGetDetailResponseDto getUserDetail(Long userId){

        User user = userQueryRepository.findUserDetail(userId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.user")));

        return UserMapper.toUserGetDetailResponseDto(user);
    }

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

    @Transactional
    public void updateUser(Long userId, UserUpdateRequestDto requestDto, String loggedInUsername) {
        log.info("서비스");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        if (!user.getUsername().equals(loggedInUsername)) {
            throw new SecurityException("본인만 정보를 수정할 수 있습니다.");
        }

        // 변경할 값이 있는 경우만 업데이트
        if (requestDto.getNickname() != null) {
            user.setNickname(requestDto.getNickname());
        }

        if (requestDto.getEmail() != null) {
            user.setEmail(requestDto.getEmail());
        }

        if (requestDto.getPassword() != null && !requestDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }

        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String targetUserId, Long requesterUserId) {
        //탈퇴 대상 회원 조회 (소프트 삭제된 회원은 제외)
        User user = userRepository.findByIdAndDeletedAtIsNull(Long.parseLong(targetUserId))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        //요청한 유저가 본인이 맞는지 다시 검증 (이중 체크)
//        if (!user.getId().equals(requesterUserId)) {
//            throw new UnauthorizedException("본인 계정만 탈퇴할 수 있습니다.");
//        }

        //소프트 삭제 처리 (삭제한 userId 기록)
        user.softDelete(requesterUserId);
    }

    private void removeRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(JwtUtil.REFRESH_TOKEN_COOKIE, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키 만료 설정

        response.addCookie(cookie);
    }

    public List<UserResponseDto> searchUsers(String username, String nickname, String roleName) {
        if (username == null && nickname == null && roleName == null) {
            throw new NotValidRequestException("검색 조건을 하나 이상 입력하세요.");
        }

        return userQueryRepository.findUsersByFilters(username, nickname, roleName);
    }



}
