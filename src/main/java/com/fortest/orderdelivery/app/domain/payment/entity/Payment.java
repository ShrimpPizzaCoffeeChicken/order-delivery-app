package com.fortest.orderdelivery.app.domain.payment.entity;

import com.fortest.orderdelivery.app.global.entity.BaseDataEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_payment")
@Entity
public class Payment extends BaseDataEntity {

    @Id
    @Column(length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 50)
    private String orderId;

    @Column(length = 50)
    private String paymentAgentId;

    @Column(length = 200, nullable = false, unique = true)
    private String paymentPid;

    @Column(length = 50)
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @Getter
    @AllArgsConstructor
    public enum Status {

        COMPLETE("완료"),
        CANCELED("취소");

        private String message;
    }
}
