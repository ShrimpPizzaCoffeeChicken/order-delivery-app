package com.fortest.orderdelivery.app.domain.menu.entity;

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
@Table(name = "p_menu_option")
@Entity
public class MenuOption extends BaseDataEntity {

    @Id
    @Column(length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer price;

    @JoinColumn(name = "p_menu_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Menu menu;

    @Column(length = 30, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ExposeStatus exposeStatus = ExposeStatus.ONSALE;

    public void updateMenuOption(String name, String description, Integer price, ExposeStatus exposeStatus) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.exposeStatus = exposeStatus;
    }
}
