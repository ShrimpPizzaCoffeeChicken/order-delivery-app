package com.fortest.orderdelivery.app.domain.payment.repository;

import com.fortest.orderdelivery.app.domain.payment.entity.PaymentAgent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentAgentRepository extends JpaRepository<PaymentAgent, String> {
    Optional<PaymentAgent> findByName(String name);
}
