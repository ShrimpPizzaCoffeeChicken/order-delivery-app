package com.fortest.orderdelivery.app.domain.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_menu_order")
@Entity
public class MenuOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String menuId;

    private String menuName;

    private Integer count;

    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @OneToMany(mappedBy = "menuOrder", cascade = CascadeType.PERSIST)
    private List<MenuOptionMenuOrder> menuOptionMenuOrderList = new ArrayList<>();

    public void bindOrder(Order order){
        this.order = order;
    }

    public void addMenuOptionMenuOrder (MenuOptionMenuOrder menuOptionMenuOrder) {
        this.menuOptionMenuOrderList.add(menuOptionMenuOrder);
        menuOptionMenuOrder.bindMenuOrder(this);
    }
}
