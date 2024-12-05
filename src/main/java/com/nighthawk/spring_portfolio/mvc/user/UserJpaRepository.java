package com.nighthawk.spring_portfolio.mvc.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long>{
    Optional<User> findByUsername(String username);
    
}
