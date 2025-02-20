package com.fortest.orderdelivery.app.domain.category.entity;

import com.fortest.orderdelivery.app.global.entity.BaseDataEntity;
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
@Table(name = "p_category")
@Entity
public class Category extends BaseDataEntity {

    @Id
    @Column(length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 100, unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<CategoryStore> categoryStoreList = new ArrayList<>();

    public void update(String categoryName) {
        this.name = categoryName;
    }

}
