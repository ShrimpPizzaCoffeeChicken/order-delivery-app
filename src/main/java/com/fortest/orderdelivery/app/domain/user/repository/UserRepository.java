package com.fortest.orderdelivery.app.domain.user.repository;

import com.fortest.orderdelivery.app.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    @Query("SELECT u FROM User u JOIN FETCH u.roleType WHERE u.username = :username")
    Optional<User> findByUsernameWithRole(@Param("username") String username);
}
