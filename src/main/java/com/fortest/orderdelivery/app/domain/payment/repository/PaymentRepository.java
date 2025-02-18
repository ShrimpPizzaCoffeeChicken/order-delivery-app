package com.fortest.orderdelivery.app.domain.payment.repository;

import com.fortest.orderdelivery.app.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {


}
