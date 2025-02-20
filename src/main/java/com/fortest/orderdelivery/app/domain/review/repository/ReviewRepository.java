package com.fortest.orderdelivery.app.domain.review.repository;

import com.fortest.orderdelivery.app.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, String> {
}
