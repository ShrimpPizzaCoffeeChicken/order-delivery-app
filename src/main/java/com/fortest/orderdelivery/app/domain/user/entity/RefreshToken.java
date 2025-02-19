package com.fortest.orderdelivery.app.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;  // 토큰을 소유한 사용자

    @Column(nullable = false, unique = true)
    private String token;  // 리프레시 토큰

    @Column(nullable = false)
    private Date expiryDate;  // 만료 시간

    public RefreshToken(String username, String refreshToken) {
    }

    public void updateToken(String token, Date expiryDate) {
        this.token = token;
        this.expiryDate = expiryDate;
    }
}
