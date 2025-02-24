package com.fortest.orderdelivery.app.global.config;

import com.fortest.orderdelivery.app.domain.user.service.UserService;
import com.fortest.orderdelivery.app.global.filter.JwtAuthenticationFilter;
import com.fortest.orderdelivery.app.global.filter.JwtAuthorizationFilter;
import com.fortest.orderdelivery.app.global.jwt.JwtUtil;
import com.fortest.orderdelivery.app.global.security.CustomAccessDeniedHandler;
import com.fortest.orderdelivery.app.global.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                    .requestMatchers(HttpMethod.GET, "/api/app/stores/{storeId}").permitAll()
//                              .requestMatchers("/api/service/users/refresh").permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/api/service/users/{userId}").authenticated()
                                .anyRequest().authenticated() // 그 외 요청은 인증 필요

       //                       .requestMatchers("/api/service/**").permitAll()
        );

        http.exceptionHandling(exceptionHandling ->
                exceptionHandling.accessDeniedHandler(new CustomAccessDeniedHandler())
        );

        http.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
