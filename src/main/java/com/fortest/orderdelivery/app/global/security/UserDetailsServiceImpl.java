package com.fortest.orderdelivery.app.global.security;

import com.fortest.orderdelivery.app.domain.user.dto.UserResponseDto;
import com.fortest.orderdelivery.app.domain.user.entity.RoleType;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.domain.user.repository.UserQueryRepository;
import com.fortest.orderdelivery.app.domain.user.repository.UserRepository;
import com.fortest.orderdelivery.app.global.exception.NotFoundException;
import com.fortest.orderdelivery.app.global.gateway.ApiGateway;
import com.fortest.orderdelivery.app.global.jwt.JwtUtil;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ApiGateway apiGateway;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final MessageUtil messageUtil;
    private final UserQueryRepository userQueryRepository;

    /**
     * **로그인 시 호출**
     * - Security에서 `username`을 기반으로 로그인할 때 DB에서 직접 조회
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // DB에서 User 엔티티 조회
        User user = userRepository.findByUsernameWithRole(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // User 엔티티를 기반으로 UserDetailsImpl 객체 생성
        return new UserDetailsImpl(user);
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserByTokenUsingRepository(String token) {
        // JWT 토큰에서 userId 추출
        Long userId = jwtUtil.getUserIdFromToken(token);
        // RDB 조회
        User user = userQueryRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(messageUtil.getMessage("not-found.user")));
        return new UserDetailsImpl(user);
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserByToken(String token) {
        // JWT 토큰에서 userId 추출
        Long userId = jwtUtil.getUserIdFromToken(token);

        // API Gateway에서 user 정보 가져오기 (DB 조회 안함)
        UserResponseDto userResponse = apiGateway.getUserByIdFromApp(userId, token);
        if (userResponse == null) {
            throw new UsernameNotFoundException("User not found: " + userId);
        }

        RoleType roleType = RoleType.builder()
                .roleName(RoleType.RoleName.getByString(messageUtil, userResponse.getRole()))
                .build();

        User user = User.builder()
                .id(userId)
                .username(userResponse.getUsername())
                .nickname(userResponse.getNickname())
                .email(userResponse.getEmail())
                .roleType(roleType)
                .isPublic(userResponse.getIsPublic())
                .build();

        user.initBaseDataByDto(userResponse);

        return new UserDetailsImpl(user);
    }
}
