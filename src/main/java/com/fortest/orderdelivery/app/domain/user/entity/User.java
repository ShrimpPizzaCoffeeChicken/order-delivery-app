package com.fortest.orderdelivery.app.domain.user.entity;

import com.fortest.orderdelivery.app.global.entity.BaseDataEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_user")
@Entity
public class User extends BaseDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String username;

    @Column(length = 100)
    private String nickname;

    @Column(length = 255, nullable = false, unique = true)
    private String email;

    @Column(length = 255)
    private String password;

    private Boolean isPublic = true;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleType roleType;

    @Builder
    public User(String username, String nickname, String email, String password, RoleType roleType, Long createdBy) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.roleType = roleType;
        this.isCreatedBy(createdBy); // 생성자에서 createdBy 설정
    }

    @Bean
    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

}
