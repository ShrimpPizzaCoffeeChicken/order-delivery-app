package com.fortest.orderdelivery.app.global.jwt;

import com.fortest.orderdelivery.app.domain.user.entity.RoleType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    //JWT 데이터
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_COOKIE = "refreshToken";
    public static final String AUTHORIZATION_KEY = "auth";
    public static final String BEARER_PREFIX = "Bearer ";
    private final long ACCESS_TOKEN_TIME  = 10 * 60 * 1000L;
    private final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000L;

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createAccessToken(Long userId, String username, String roleName) {
        long now = System.currentTimeMillis();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username)
                        .claim("userId", userId)
                        .claim(AUTHORIZATION_KEY, roleName)
                        .setIssuedAt(new Date(now))
                        .setExpiration(new Date(now + ACCESS_TOKEN_TIME))
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }


    public String createRefreshToken(Long userId, String username) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_TIME))
                .setIssuedAt(now)
                .signWith(key, signatureAlgorithm)
                .compact();
    }


    public void addAccessTokenToHeader(String token, HttpServletResponse response) {
        response.setHeader(AUTHORIZATION_HEADER, token);
    }

    public void addRefreshTokenToCookie(String refreshToken, HttpServletResponse response) {
        try {
            refreshToken = URLEncoder.encode(refreshToken, "utf-8").replaceAll("\\+", "%20");
            Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, refreshToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge((int) (REFRESH_TOKEN_TIME / 1000)); // 7일
            response.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
    }

    // JWT 토큰 substring
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        logger.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token");
        } catch (JwtException e) {
            logger.error("Invalid JWT token");
        }
        return false;
    }


    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

//    public String getUsernameFromToken(String token) {
//        return getUserInfoFromToken(token).getSubject();
//    }

    public Long getUserIdFromToken(String token) {
        return getUserInfoFromToken(token).get("userId", Long.class);
    }

    public String getAccessTokenFromHeader(HttpServletRequest req) {
        String bearerToken = req.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return substringToken(bearerToken);
        }
        return null;
    }

    public String getRefreshTokenFromCookie(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                    try {
                        return URLDecoder.decode(cookie.getValue(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        }
        return null;
    }

}
