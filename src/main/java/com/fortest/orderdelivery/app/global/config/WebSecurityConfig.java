package com.fortest.orderdelivery.app.global.config;

import com.fortest.orderdelivery.app.domain.user.service.UserService;
import com.fortest.orderdelivery.app.global.dto.CommonDto;
import com.fortest.orderdelivery.app.global.filter.JwtAuthenticationFilter;
import com.fortest.orderdelivery.app.global.filter.JwtAuthorizationFilter;
import com.fortest.orderdelivery.app.global.jwt.JwtUtil;
import com.fortest.orderdelivery.app.global.security.CustomAccessDeniedHandler;
import com.fortest.orderdelivery.app.global.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;

    private static final String MISSING_HEADER_MESSAGE = "Missing or invalid Authorization header.";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
        throws Exception {
        return configuration.getAuthenticationManager();
    }

    //    @Bean
//    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
//        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, userService);
//        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
//        return filter;
//    }
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(UserService userService)
        throws Exception { //메서드에서 주입
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, userService);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        http.sessionManagement(sessionManagement ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests(authorizeHttpRequests ->
                authorizeHttpRequests
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // 정적 리소스 허용
                    .requestMatchers("/api/service/users/login", "/api/service/users/signup","/api/service/users/check-username","/api/service/users/refresh").permitAll() // 로그인 & 회원가입만 인증 없이 허용
                    .requestMatchers("/api/service/users/protected-resource").authenticated()
                    .requestMatchers("/api/service/areas/**").authenticated()
                    .requestMatchers("/api/app/users/*").permitAll()
                    .requestMatchers("/api/app/menus/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/service/stores/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/service/categories").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/app/orders/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/app/deliveries/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/app/stores/{storeId}").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/app/stores/{storeId}/menus/valid").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/app/menus/options/valid").permitAll()
//                   .requestMatchers("/api/service/users/refresh").permitAll()
                    .requestMatchers(HttpMethod.DELETE, "/api/service/users/{userId}").authenticated()
                    .anyRequest().authenticated() // 그 외 요청은 인증 필요

       //                       .requestMatchers("/api/service/**").permitAll()
        );

        http
                // 권한 없음
                .exceptionHandling(exceptionHandling ->
                    exceptionHandling.accessDeniedHandler(new CustomAccessDeniedHandler())
                )
                // 인증 헤더 누락 시
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json");
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    CommonDto<Object> body = CommonDto.builder()
                            .code(HttpStatus.FORBIDDEN.value())
                            .message(MISSING_HEADER_MESSAGE)
                            .data(JSONObject.NULL)
                            .build();
                    response.getWriter().write(new JSONObject(body).toString());
                }));


        http.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
