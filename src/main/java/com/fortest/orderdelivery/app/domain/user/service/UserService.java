package com.fortest.orderdelivery.app.domain.user.service;

import com.fortest.orderdelivery.app.domain.user.dto.SignupRequestDto;
import com.fortest.orderdelivery.app.domain.user.entity.RoleType;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.domain.user.repository.RoleTypeRepository;
import com.fortest.orderdelivery.app.domain.user.repository.UserRepository;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleTypeRepository roleTypeRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CommonDto<Void> signup (SignupRequestDto requestDto){

        // TODO : create By 추가 해야함

        // RoleType 확인 (CUSTOMER, OWNER만 허용)
        RoleType roleType = roleTypeRepository.findById(requestDto.getRoleId())
                .orElseThrow(() -> new BusinessLogicException("유효하지 않은 Role ID 입니다."));

        if (!roleType.getName().equals("CUSTOMER") && !roleType.getName().equals("OWNER")) {
            throw new IllegalArgumentException("해당 역할로는 가입할 수 없습니다.");
        }

        User user = User.builder()
                .username(requestDto.getUsername())
                .nickname(requestDto.getNickname())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .roleType(roleType)
                .build();

        userRepository.save(user);

        // 5. 응답 메시지 생성 (닉네임 포함)
        String successMessage = user.getNickname() + "님의 회원가입이 완료되었습니다.";

        return new CommonDto<>(successMessage, 200, null);
    }

}
