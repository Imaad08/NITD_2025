package com.nighthawk.spring_portfolio.mvc.stocks;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(@Param("username") String username);
}