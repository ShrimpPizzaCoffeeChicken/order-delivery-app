package com.fortest.orderdelivery.app.domain.user.service;

import com.fortest.orderdelivery.app.domain.user.dto.SignupRequestDto;
import com.fortest.orderdelivery.app.domain.user.entity.RoleType;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.domain.user.repository.RoleTypeRepository;
import com.fortest.orderdelivery.app.domain.user.repository.UserRepository;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @Transactional
    public User signup (SignupRequestDto requestDto){

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

}
