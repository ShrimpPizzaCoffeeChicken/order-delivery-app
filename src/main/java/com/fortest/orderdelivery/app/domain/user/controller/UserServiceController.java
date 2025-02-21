package com.fortest.orderdelivery.app.domain.user.controller;

import com.fortest.orderdelivery.app.domain.user.dto.LoginResponseDto;
import com.fortest.orderdelivery.app.domain.user.dto.SignupRequestDto;
import com.fortest.orderdelivery.app.domain.user.dto.UserGetDetailResponseDto;
import com.fortest.orderdelivery.app.domain.user.dto.UserUpdateRequestDto;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.domain.user.service.UserService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/service/")
@RequiredArgsConstructor
public class UserServiceController {

    private final UserService userService;

    // 토큰 재발급
    @PostMapping("/users/refresh")
    public ResponseEntity<CommonDto<LoginResponseDto>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(userService.refreshToken(request, response));
    }

    // 회원가입
    @PostMapping("/users/signup")
    public ResponseEntity<CommonDto<Void>> signup(@RequestBody SignupRequestDto requestDto) {
        User user = userService.signup(requestDto);
        String successMessage = user.getNickname() + "님의 회원가입이 완료되었습니다.";

        userService.isCreatedBy(user);

        return ResponseEntity.ok(
                CommonDto.<Void>builder()
                        .code(HttpStatus.OK.value())
                        .message(successMessage)
                        .data(null)
                        .build()
        );

    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<CommonDto<UserGetDetailResponseDto>> getUserDetail (@PathVariable("userId") Long userId) {
        UserGetDetailResponseDto userDetailResponseDto = userService.getUserDetail(userId);

        return ResponseEntity.ok(
                CommonDto.<UserGetDetailResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message("Success")
                        .data(userDetailResponseDto)
                        .build()
        );
    }

    @GetMapping("/users/check-username")
    public ResponseEntity<CommonDto<Map<String, Object>>> checkUsername(@RequestParam(name = "username") String username) {
        return ResponseEntity.ok(userService.checkUsernameAvailability(username));
    }

    @PostMapping("/users/logout")
    public CommonDto<String> logout(HttpServletRequest request, HttpServletResponse response) {
        return userService.logout(request, response);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<CommonDto<Void>> updateUser(
            @PathVariable("userId") Long userId,
            @RequestBody UserUpdateRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("컨트롤러");
        // JWT에서 가져온 username
        String loggedInUsername = userDetails.getUsername();

        userService.updateUser(userId, requestDto, loggedInUsername);

        return ResponseEntity.ok(
                CommonDto.<Void>builder()
                        .code(HttpStatus.OK.value())
                        .message("사용자 정보가 성공적으로 수정되었습니다.")
                        .data(null)
                        .build()
        );
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<CommonDto<Void>> deleteUser(
            @PathVariable("userId") String targetUserId, // 탈퇴할 대상 userId
            @AuthenticationPrincipal UserDetailsImpl userDetails // 현재 로그인한 사용자 정보 가져오기
    ) {
        Long requesterUserId = userDetails.getUserId(); // 로그인한 사용자의 userId 가져오기

        // 본인이 맞는지 검증
        if (!targetUserId.equals(requesterUserId.toString())) {
            throw new BusinessLogicException("본인 계정만 탈퇴할 수 있습니다.");
        }

        // 회원 탈퇴 실행 (삭제한 userId 저장)
        userService.deleteUser(targetUserId, requesterUserId);

        return ResponseEntity.ok(CommonDto.<Void>builder()
                .message("회원 탈퇴 완료")
                .code(HttpStatus.OK.value())
                .data(null)
                .build());
    }


}
