package com.fortest.orderdelivery.app.domain.category.entity;

import com.fortest.orderdelivery.app.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_category_store")
@Entity
public class CategoryStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @JoinColumn(name = "store_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;

    public void bindCategory(Category category) {
        this.category = category;
        category.getCategoryStoreList().add(this);
    }

    public void bindStore(Store store) {
        this.store = store;
        store.getCategoryStoreList().add(this);
    }
}
