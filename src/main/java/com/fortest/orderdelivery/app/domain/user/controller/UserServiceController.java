package com.fortest.orderdelivery.app.domain.user.controller;

import com.fortest.orderdelivery.app.domain.order.dto.OrderGetListRequestDto;
import com.fortest.orderdelivery.app.domain.user.dto.*;
import com.fortest.orderdelivery.app.domain.user.service.UserService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.security.UserDetailsImpl;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // 회원 권한 업데이트
    @PreAuthorize("hasRole('MANAGER') or hasRole('MASTER')")
    @PatchMapping("/users/{userId}/rolls")
    public ResponseEntity<CommonDto<UserUpdateRollResponseDto>> updateRoll(@PathVariable("userId") Long userId,
                                                                           @Valid @RequestBody UserUpdateRollRequestDto requestDto,
                                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserUpdateRollResponseDto responseDto = userService.updateRoll(userId, requestDto.getToRoll(), userDetails.getUser());
        return ResponseEntity.ok(
                CommonDto.<UserUpdateRollResponseDto>builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(responseDto)
                        .build()
        );
    }

    // 토큰 재발급
    @PostMapping("/users/refresh")
    public ResponseEntity<CommonDto<Object>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(userService.refreshToken(request, response));
    }

    // 회원가입
    @PostMapping("/users/signup")
    public ResponseEntity<CommonDto<UserSignupResponseDto>> signup(@Valid @RequestBody SignupRequestDto requestDto) {
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
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<CommonDto<UserGetDetailResponseDto>> getUserDetail (@PathVariable("userId") Long userId,
                                                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserGetDetailResponseDto userDetailResponseDto = userService.getUserDetail(userId);

        return ResponseEntity.ok(
                CommonDto.<UserGetDetailResponseDto> builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(userDetailResponseDto)
                        .build()
        );
    }

    //아이디 중복체크
    @GetMapping("/users/check-username")
    public ResponseEntity<CommonDto<Map<String, Object>>> checkUsername(@RequestParam(name = "username") String username) {
        return ResponseEntity.ok(userService.checkUsernameAvailability(username));
    }

    @PostMapping("/users/logout")
    public ResponseEntity<CommonDto<String>> logout(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(userService.logout(request, response));
    }

    //회원정보 수정
    @PatchMapping("/users/{userId}")
    public ResponseEntity<CommonDto<Void>> updateUser(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody UserUpdateRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        // JWT에서 가져온 username
        String loggedInUsername = userDetails.getUsername();

        userService.updateUser(userId, requestDto, loggedInUsername);

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

    //관리자 회원정보 조회
    @PreAuthorize("hasRole('MANAGER') or hasRole('MASTER')" )
    @GetMapping("/users/search")
    public ResponseEntity<CommonDto<UserGetListResponseDto>> searchUsers(
            @Valid OrderGetListRequestDto requestDto) {

        UserGetListResponseDto userList = userService.searchUsers(
                requestDto.getPage(),
                requestDto.getSize(),
                requestDto.getOrderby(),
                requestDto.getSort(),
                requestDto.getSearch()
        );

        return ResponseEntity.ok(
                CommonDto.<UserGetListResponseDto>builder()
                        .code(HttpStatus.OK.value())
                        .message(messageUtil.getSuccessMessage())
                        .data(userList)
                        .build()
        );
    }

    //사용자 본인 정보 조회
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('OWNER') or hasRole('MANAGER') or hasRole('MASTER')")
    @GetMapping("/users/search-me")
    public ResponseEntity<CommonDto<UserResponseDto>> getMyInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("본인 정보 조회 요청 - username: {}", userDetails.getUsername());

        UserResponseDto userInfo = userService.getUserDetailMe(userDetails.getUserId());

        return ResponseEntity.ok(
                CommonDto.<UserResponseDto>builder()
                        .message(messageUtil.getSuccessMessage())
                        .code(HttpStatus.OK.value())
                        .data(userInfo)
                        .build()
        );
    }


}