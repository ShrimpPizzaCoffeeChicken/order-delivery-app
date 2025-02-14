package com.fortest.orderdelivery.app.domain.store.entity;

import com.fortest.orderdelivery.app.domain.area.entity.Area;
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
    @ManyToOne
    private Area area;

    @Column(length = 200)
    private String detailAddress;

    @Column(length = 100)
    private String ownerName;
}
