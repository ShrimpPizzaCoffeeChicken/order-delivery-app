package com.fortest.orderdelivery.app.domain.user.controller;

import com.fortest.orderdelivery.app.domain.user.dto.*;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.domain.user.service.UserService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.security.UserDetailsImpl;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/service/")
@RequiredArgsConstructor
public class UserServiceController {

    private final MessageUtil messageUtil;
    private final UserService userService;

    // 토큰 재발급
    @PostMapping("/users/refresh")
    public ResponseEntity<CommonDto<LoginResponseDto>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(userService.refreshToken(request, response));
    }

    // 회원가입
    @PostMapping("/users/signup")
    public ResponseEntity<CommonDto<UserSignupResponseDto>> signup(@RequestBody SignupRequestDto requestDto) {
        UserSignupResponseDto responseDto = userService.signup(requestDto);

        return ResponseEntity.ok(
                CommonDto.<UserSignupResponseDto>builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getMessage("signup.success", responseDto.getNickname()))
                        .data(responseDto)
                        .build()
        );

    }

    //사용자 정보 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<CommonDto<UserGetDetailResponseDto>> getUserDetail (@PathVariable("userId") Long userId) {
        UserGetDetailResponseDto userDetailResponseDto = userService.getUserDetail(userId);

        return ResponseEntity.ok(
                CommonDto.<UserGetDetailResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(userDetailResponseDto)
                        .build()
        );
    }

    @GetMapping("/users/check-username")
    public ResponseEntity<CommonDto<Map<String, Object>>> checkUsername(@RequestParam(name = "username") String username) {
        return ResponseEntity.ok(userService.checkUsernameAvailability(username));
    }

    @PostMapping("/users/logout")
    public ResponseEntity<CommonDto<String>> logout(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(userService.logout(request, response));
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<CommonDto<Void>> updateUser(
            @PathVariable("userId") Long userId,
            @RequestBody UserUpdateRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("회원 정보 수정 요청 - userId: {}", userId);
        // JWT에서 가져온 username
        //String loggedInUsername = userDetails.getUsername();

        userService.updateUser(userId, requestDto, userDetails.getUsername());

        return ResponseEntity.ok(
                CommonDto.<Void>builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(null)
                        .build()
        );
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<CommonDto<Void>> deleteUser(
            @PathVariable("userId") Long userId, // 탈퇴할 대상 userId
            @AuthenticationPrincipal UserDetailsImpl userDetails // 현재 로그인한 사용자 정보 가져오기
    ) {
//        // 본인이 맞는지 검증
//        if (!targetUserId.equals(requesterUserId.toString())) {
//            throw new BusinessLogicException("본인 계정만 탈퇴할 수 있습니다.");
//        }
        // 본인 확인 후 탈퇴 수행
        if (!userId.equals(userDetails.getUserId())) {
            throw new BusinessLogicException(messageUtil.getMessage("delete.user.forbidden"));
        }

        // 회원 탈퇴 실행 (삭제한 userId 저장)
        userService.deleteUser(userId, userDetails.getUserId());

        return ResponseEntity.ok(CommonDto.<Void>builder()
                .message(messageUtil.getSuccessMessage())
                .code(HttpStatus.OK.value())
                .data(null)
                .build());
    }

    @GetMapping("/users/search")
    public ResponseEntity<CommonDto<List<UserResponseDto>>> searchUsers(
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "nickname", required = false) String nickname,
            @RequestParam(name = "role", required = false) String role) {

        log.info("회원 검색 요청 - username: {}, nickname: {}, role: {}", username, nickname, role);

        List<UserResponseDto> users = userService.searchUsers(username, nickname, role);

//        if (users.isEmpty()) {
//            throw new NotFoundException("해당 조건에 맞는 회원이 없습니다.");
//        }

        return ResponseEntity.ok(
                CommonDto.<List<UserResponseDto>>builder()
                        .message(messageUtil.getSuccessMessage())
                        .code(HttpStatus.OK.value())
                        .data(users)
                        .build()
        );
    }


}
