package com.fortest.orderdelivery.app.domain.delivery.entity;

import com.fortest.orderdelivery.app.global.entity.BaseDataEntity;
import com.fortest.orderdelivery.app.global.exception.BusinessLogicException;
import com.fortest.orderdelivery.app.global.util.MessageUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_delivery")
@Entity
public class Delivery extends BaseDataEntity {

    @Id
    @Column(length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 50)
    private String customerName;

    @Column(length = 50)
    private String orderId;

    @Column(length = 100)
    private String address;

    @Column(length = 50)
    @Enumerated(value = EnumType.STRING)
    private Status status;

    public void updateStatus (Status status) {
        this.status = status;
    }

    @Getter
    @AllArgsConstructor
    public enum Status {

        START("배달시작"),
        END("배달완료"),
        CANCEL("취소");

        private final String message;

        public static Status getByString (MessageUtil messageUtil, String statusString) {
            try {
                return Status.valueOf(statusString);
            } catch (IllegalArgumentException e) {
                throw new BusinessLogicException(messageUtil.getMessage("app.delivery.status.not-found"));
            }
        }
    }
}
