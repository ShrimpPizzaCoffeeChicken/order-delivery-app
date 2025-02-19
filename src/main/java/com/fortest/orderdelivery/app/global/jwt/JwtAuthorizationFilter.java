package com.fortest.orderdelivery.app.global.jwt;

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

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtAuthorization 필터 실행됨");
        // Access Token 가져오기
        String accessToken = jwtUtil.getAccessTokenFromHeader(req);
        log.info("가져온 Access Token: {}", accessToken);

        String refreshToken = jwtUtil.getRefreshTokenFromCookie(req);
        log.info("가져온 Refresh Token: {}", refreshToken);

        if (StringUtils.hasText(accessToken)) {
            //Access Token 검증
//            if (jwtUtil.validateToken(accessToken)) {
//                //토큰에서 사용자 정보 추출
//                Claims claims = jwtUtil.getUserInfoFromToken(accessToken);
//                log.info("JWT에서 추출한 username: " + claims.getSubject());
//
//                try {
//                    setAuthentication(claims.getSubject());
//                } catch (Exception e) {
//                    log.error(e.getMessage());
//                    return;
//                }
//            } else {
//                log.warn("Access Token이 만료됨. 클라이언트는 /api/users/refresh 요청 필요");
//            }
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

            Claims info = jwtUtil.getUserInfoFromToken(accessToken);
            log.info("JWT에서 추출한 username: " + info.getSubject());

            try {
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error("인증 실패: " + e.getMessage());
                return;
            }
        }

        // 다음 필터 실행
        filterChain.doFilter(req, res);
    }


    // 인증 처리
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
