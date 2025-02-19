package com.fortest.orderdelivery.app.domain.ai.repository;

import com.fortest.orderdelivery.app.domain.ai.entity.AiRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiRequestRepository extends JpaRepository<AiRequest, String> {
}
