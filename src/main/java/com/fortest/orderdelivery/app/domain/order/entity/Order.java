package com.fortest.orderdelivery.app.domain.order.entity;

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
@Table(name = "p_order")
@Entity
public class Order extends BaseDataEntity {

    @Id
    @Column(length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 50)
    private String storeId;

    @Column(length = 100)
    private String storeName;

    private Integer totalPrice;

    @Column(length = 50)
    private String customerName;

    @Column(length = 50, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private OrderType orderType;

    @Getter
    @AllArgsConstructor
    public enum OrderType {
        DELIVERY("배달"),
        INSTORE("매장직접주문");

        private String message;
    }
}
