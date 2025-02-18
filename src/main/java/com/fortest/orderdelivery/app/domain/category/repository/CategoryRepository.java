package com.fortest.orderdelivery.app.domain.category.repository;

import com.fortest.orderdelivery.app.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
}
