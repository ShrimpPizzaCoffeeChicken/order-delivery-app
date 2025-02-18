package com.fortest.orderdelivery.app.domain.user.repository;

import com.fortest.orderdelivery.app.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
}
