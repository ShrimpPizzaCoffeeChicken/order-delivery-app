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
import com.fortest.orderdelivery.app.global.jwt.JwtUtil;
import com.fortest.orderdelivery.app.global.util.JpaUtil;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

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

    // 유저 권한 업데이트
    @Transactional
    public UserUpdateRollResponseDto updateRoll(Long targetUserId, String toRollString, User user) {

        if (toRollString == null || toRollString.trim().isEmpty()) {
            throw new BusinessLogicException("toRoll 값이 null 또는 비어 있습니다.");
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("app.user.not-found-user")));
        String fromRoll = targetUser.getRoleType().getRoleName().name();

        RoleType.RoleName toRoleName = RoleType.RoleName.getByString(messageUtil, toRollString);

        RoleType newRoleType = roleTypeRepository.findByRoleName(toRoleName)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("app.user.not-found-role-name")));
        String toRoll = newRoleType.getRoleName().name();

        targetUser.updateRollType(newRoleType);
        targetUser.isUpdatedNow(user.getId());

        return new UserUpdateRollResponseDto(fromRoll, toRoll);
    }

    // 로그인 관련 기능 (토큰 재발급)
    @Transactional
    public CommonDto<Object> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtil.getRefreshTokenFromCookie(request);
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            return new CommonDto<>("Invalid Refresh Token", HttpStatus.UNAUTHORIZED.value(), null);
        }

        Claims claims = jwtUtil.getUserInfoFromToken(refreshToken);
        String username = claims.getSubject();

        User user = userRepository.findByUsernameWithRole(username)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        String newAccessToken = jwtUtil.createAccessToken(user.getId(), username, user.getRoleType().getRoleName().name());

        jwtUtil.addAccessTokenToHeader(newAccessToken, response);

        return CommonDto.<Object>builder()
                .message("새로운 Access Token 발급")
                .code(HttpStatus.OK.value())
                .data(null)
                .build();
    }

    @Transactional
    public UserSignupResponseDto signup(SignupRequestDto requestDto) {

        validateUsername(requestDto.getUsername());
        validatePassword(requestDto.getPassword());

        RoleType roleType = roleTypeRepository.findByRoleName(RoleType.RoleName.CUSTOMER)
                .orElseThrow(() -> new BusinessLogicException(messageUtil.getMessage("not-found.role")));

        User user = User.builder()
                .username(requestDto.getUsername())
                .nickname(requestDto.getNickname())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .roleType(roleType)
                .isPublic(true)
                .build();

        userRepository.save(user);
        userRepository.flush();

        userQueryRepository.insertUpdatedAt(user);

        return UserMapper.fromUserToUserSignupResponseDto(user);
    }

    @Transactional
    public UserGetDetailResponseDto getUserDetail(Long userId){

        User user = userQueryRepository.findUserDetail(userId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.user")));

        return UserMapper.toUserGetDetailResponseDto(user);
    }

    @Transactional(readOnly = true)
    public CommonDto<Map<String, Object>> checkUsernameAvailability(String username) {
        //존재하는 아이디이면 false, 존재하지 않으면 true
        boolean isAvailable = !userRepository.existsByUsername(username);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("username", username);
        responseData.put("is-available", isAvailable);

        String messageKey = isAvailable ? "user.username.available" : "user.username.not-available";

        //응답 객체 생성 및 반환
        return new CommonDto<>(messageUtil.getMessage(messageKey),
                isAvailable ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value(),
                responseData);
    }

    @Transactional
    public CommonDto<String> logout(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = jwtUtil.getRefreshTokenFromCookie(request);

        if (refreshToken == null) {
            return new CommonDto<>("Refresh Token이 존재하지 않습니다.", HttpStatus.BAD_REQUEST.value(), null);
        }

        removeRefreshTokenCookie(response);

        return new CommonDto<>("로그아웃 완료", HttpStatus.OK.value(), "로그아웃 성공");
    }

    @Transactional
    public void updateUser(Long userId, UserUpdateRequestDto requestDto, String loggedInUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("not-found.user"));

        if (!user.getUsername().equals(loggedInUsername)) {
            throw new SecurityException(messageUtil.getMessage("update.user.forbidden"));
        }

        // 비밀번호 암호화 추가
        if (requestDto.getPassword() != null && !requestDto.getPassword().isBlank()) {
            requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }

        user.updateUserInfo(requestDto);
        user.isUpdatedNow(userId);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long targetUserId, Long requesterUserId) {
        //탈퇴 대상 회원 조회 (소프트 삭제된 회원은 제외)
        User user = userRepository.findByIdAndDeletedAtIsNull(targetUserId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

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

    @Transactional
    public UserGetListResponseDto searchUsers(Integer page, Integer size, String orderby, String sort, String search) {

        PageRequest pageable = JpaUtil.getNormalPageable(page, size, orderby, sort);
        Page<User> userPage = userQueryRepository.findUserList(pageable, search);

        return UserMapper.pageToGetUserListDto(userPage, search);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserDetailMe(Long userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new NotFoundException("not-found.user"));

        return UserResponseDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .role(user.getRoleType().getRoleName().name())
                .isPublic(user.getIsPublic())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .createdBy(user.getCreatedBy())
                .updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null)
                .updatedBy(user.getUpdatedBy())
                .build();
    }

    private void validateUsername(String username) {
        String usernameRegex = "^[a-z0-9]{4,10}$";
        if (!username.matches(usernameRegex)) {
            throw new BusinessLogicException(messageUtil.getMessage("invalid.username"));
        }
    }

    private void validatePassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$";
        if (!password.matches(passwordRegex)) {
            throw new BusinessLogicException(messageUtil.getMessage("invalid.password"));
        }
    }


}
