package com.fortest.orderdelivery.app.domain.delivery.repository;

import com.fortest.orderdelivery.app.domain.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, String> {
}
