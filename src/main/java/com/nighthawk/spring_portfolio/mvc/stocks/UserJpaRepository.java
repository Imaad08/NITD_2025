package com.nighthawk.spring_portfolio.mvc.stocks;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
}