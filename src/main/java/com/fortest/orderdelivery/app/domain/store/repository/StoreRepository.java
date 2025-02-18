package com.fortest.orderdelivery.app.domain.store.repository;

import com.fortest.orderdelivery.app.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, String> {
}
