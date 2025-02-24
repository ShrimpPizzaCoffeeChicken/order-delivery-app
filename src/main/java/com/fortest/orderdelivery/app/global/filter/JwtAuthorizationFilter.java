package com.fortest.orderdelivery.app.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.jwt.JwtUtil;
import com.fortest.orderdelivery.app.global.security.UserDetailsServiceImpl;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    private static final String REPOSITORY_FIND_URL = "/api";
    private static final String APP_URL_SUFFIX = "/app";
    private static final String SERVICE_URL_SUFFIX = "/service";
    private static final String USER_API_SUFFIX = "/users";

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = req.getRequestURI();
        Set<String> excludeUrls = new HashSet<>();
        excludeUrls.add("/api/service/users/login");
        excludeUrls.add("/api/service/users/signup");
        excludeUrls.add("/api/service/users/check-username");
        excludeUrls.add("/api/service/users/refresh");

        //예외 처리할 URL은 필터 건너뜀
        if (excludeUrls.contains(requestURI)) {
            filterChain.doFilter(req, res);
            return;
        }

        String accessToken = jwtUtil.getAccessTokenFromHeader(req);

        if (StringUtils.hasText(accessToken)) {
            if (!jwtUtil.validateToken(accessToken)) {
                log.error("Access Token이 만료됨");

                sendJsonResponse(res, HttpStatus.UNAUTHORIZED, "access.token.expired", null);

                return;
            }

            //토큰이 유효하면 추가인증처리
            try {
                String targetUrl = req.getRequestURL().toString();
                boolean isFindRepositoryUrl = isFindRepositoryTargetUrl(
                        targetUrl
                );

                log.info("isFindRepositoryUrl : {}", isFindRepositoryUrl);

                setAuthenticationFromToken(accessToken, isFindRepositoryUrl);
            } catch (Exception e) {
                log.error("인증 실패: ", e);
                sendJsonResponse(res, HttpStatus.UNAUTHORIZED, "authentication.failed", null);
                return;
            }
        } else {
            log.error("Access Token이 요청 헤더에 없음");
            sendJsonResponse(res, HttpStatus.UNAUTHORIZED, "access.token.missing", null);
        }
        // 다음 필터 실행
        filterChain.doFilter(req, res);
    }

    private void sendJsonResponse(HttpServletResponse res, HttpStatus status, String message, Object data) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.setStatus(status.value());

        CommonDto<Object> responseDto = CommonDto.builder()
                .message(message)
                .code(status.value())
                .data(data)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(responseDto);

        PrintWriter writer = res.getWriter();
        writer.write(jsonResponse);
        writer.flush();
    }

    public void setAuthenticationFromToken(String token, Boolean isUseRepository) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthenticationFromToken(token, isUseRepository);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private Authentication createAuthenticationFromToken(String token, Boolean isUseRepository) {

        UserDetails userDetails = null;
        if (isUseRepository) {
            userDetails = userDetailsService.loadUserByTokenUsingRepository(token);
        } else {
            // `loadUserByToken`을 사용하여 API Gateway에서 유저 정보 가져오기
            userDetails = userDetailsService.loadUserByToken(token);
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
    }


    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
    }

    /**
     * 요청 URL 을 확인 한 뒤 유저정보를 repository 에서 조회해야하는지 판단
     *
     * @param currentUrl
     * @return
     */
    private boolean isFindRepositoryTargetUrl(String currentUrl) {
        int i = currentUrl.lastIndexOf(REPOSITORY_FIND_URL);
        String substring = currentUrl.substring(i).replace(REPOSITORY_FIND_URL, "");
        if (substring.startsWith(APP_URL_SUFFIX)) {
            substring = substring.replace(APP_URL_SUFFIX, "");
        } else {
            substring = substring.replace(SERVICE_URL_SUFFIX, "");
        }

        log.info("targetUrlSubString: {}", substring);
        return substring.startsWith(USER_API_SUFFIX);
    }
}
