package com.fortest.orderdelivery.app.domain.order.entity;

import com.fortest.orderdelivery.app.global.entity.BaseDataEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@ToString
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
    private OrderStatus orderStatus = OrderStatus.WAIT;

    @Column(length = 50, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private OrderType orderType = OrderType.INSTORE;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<MenuOrder> menuOrderList = new ArrayList<>();

    @Getter
    @AllArgsConstructor
    public enum OrderStatus {
        WAIT("대기"),
        PAYED("결제완료"),
        COMPLETE("배달완료"),
        FAIL("주문실패"),
        CANCEL("주문취소");

        private String message;
    }

    @Getter
    @AllArgsConstructor
    public enum OrderType {
        DELIVERY("배달"),
        INSTORE("매장직접주문");

        private String message;
    }

    public void updateTotalPrice (Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void updateStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void addMenuOrder (MenuOrder menuOrder) {
        this.menuOrderList.add(menuOrder);
        menuOrder.bindOrder(this);
    }

    public void addMenuOrderList (List<MenuOrder> menuOrderList) {
        this.menuOrderList.addAll(menuOrderList);
    }

    public static OrderStatus getOrderStatusByString(String orderStatusString) {
        return OrderStatus.valueOf(orderStatusString);
    }

    public static OrderType getOrderTypeByString(String orderTypeString) {
        return OrderType.valueOf(orderTypeString);
    }
}
