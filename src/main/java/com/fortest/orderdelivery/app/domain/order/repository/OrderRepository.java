package com.fortest.orderdelivery.app.domain.order.repository;

import com.fortest.orderdelivery.app.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
