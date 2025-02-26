package com.fortest.orderdelivery.app.domain.store.entity;

import com.fortest.orderdelivery.app.domain.area.entity.Area;
import com.fortest.orderdelivery.app.domain.category.entity.CategoryStore;
import com.fortest.orderdelivery.app.global.entity.BaseDataEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_store")
@Entity
public class Store extends BaseDataEntity {

    @Id
    @Column(length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 100, nullable = false)
    private String name;

    @JoinColumn(name = "area_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Area area;

    @Column(length = 200)
    private String detailAddress;

    @Column(length = 100)
    private String ownerName;

    @OneToMany(mappedBy = "store", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<CategoryStore> categoryStoreList = new ArrayList<>();

    public void update(String storeName, Area area, String detailAddress, String ownerName) {
        this.name = storeName;
        this.area = area;
        this.detailAddress = detailAddress;
        this.ownerName = ownerName;
    }
}
