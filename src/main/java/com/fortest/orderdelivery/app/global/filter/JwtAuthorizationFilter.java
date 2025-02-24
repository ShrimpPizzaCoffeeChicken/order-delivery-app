package com.fortest.orderdelivery.app.global.filter;

import com.fortest.orderdelivery.app.global.jwt.JwtUtil;
import com.fortest.orderdelivery.app.global.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

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
        log.info("authorizationFilter=============");

            // Access Token 가져오기
            String accessToken = jwtUtil.getAccessTokenFromHeader(req);

            if (StringUtils.hasText(accessToken)) {
                if (!jwtUtil.validateToken(accessToken)) {
                    log.error("Access Token이 만료됨");

                    // 만료된 경우, 401 응답을 JSON으로 반환
                    res.setContentType("application/json");
                    res.setCharacterEncoding("UTF-8");
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                    PrintWriter writer = res.getWriter();
                    writer.write("{ \"message\": \"Access Token Expired\", \"code\": 401 }");
                    writer.flush();
                    return;
                }

                //Claims info = jwtUtil.getUserInfoFromToken(accessToken);

                try {
                    // url 매칭 확인 : 특정 http 메소드 + 특정 url 이면 repository 에서 조회해야한다고 판단
                    String targetUrl = req.getRequestURL().toString();
                    boolean isFindRepositoryUrl = isFindRepositoryTargetUrl(
                        targetUrl
                    );

                    log.info("isFindRepositoryUrl : {}", isFindRepositoryUrl);

                    setAuthenticationFromToken(accessToken, isFindRepositoryUrl);
                } catch (Exception e) {
                    log.error("인증 실패: ", e);
                    return;
                }


            }
        // 다음 필터 실행
        filterChain.doFilter(req, res);
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
