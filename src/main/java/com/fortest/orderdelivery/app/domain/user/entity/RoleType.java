package com.fortest.orderdelivery.app.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_role_type")
@Entity
public class RoleType {
    @Id
    @Column(length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 50, nullable = false, unique = true)
    @Enumerated(value = EnumType.STRING)
    private RoleName roleName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Getter
    @AllArgsConstructor
    public enum RoleName {
        CUSTOMER("일반사용자"),
        OWNER("가게사장"),
        MANAGER("매니저"),
        MASTER("마스터");

        private String message;
    }
}
