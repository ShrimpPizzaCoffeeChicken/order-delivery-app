package com.fortest.orderdelivery.app.domain.menu.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fortest.orderdelivery.app.domain.order.entity.MenuOrder;
import com.fortest.orderdelivery.app.global.entity.BaseDataEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_menu")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Menu extends BaseDataEntity {

    @Id
    @Column(length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer price;

    @Column(length = 50, nullable = false)
    private String storeId;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MenuOption> menuOptionList = new ArrayList<>();

    @Column(length = 30, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ExposeStatus exposeStatus = ExposeStatus.ONSALE;

    public void updateMenu(String name, String description, Integer price, ExposeStatus exposeStatus) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.exposeStatus = exposeStatus;
    }

    public void updateMenuOption(MenuOption menuOption) {
        log.info("menuOptionList : {} ",this.menuOptionList);
        log.info("menuOption : {} ",menuOption);

        this.menuOptionList.add(menuOption);
        menuOption.updateMenu(this);
    }
}
