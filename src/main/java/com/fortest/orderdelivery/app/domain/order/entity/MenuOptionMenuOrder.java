package com.fortest.orderdelivery.app.domain.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_menu_option_menu_order")
@Entity
public class MenuOptionMenuOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String menuOptionId;

    @Column(length = 100)
    private String menuOptionName;

    private Integer menuOptionCount;

    private Integer menuOptionPrice;

    @ManyToOne
    @JoinColumn(name = "menu_order_id")
    private MenuOrder menuOrder;
}
