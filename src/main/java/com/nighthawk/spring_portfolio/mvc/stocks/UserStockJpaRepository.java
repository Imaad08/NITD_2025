package com.nighthawk.spring_portfolio.mvc.stocks;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStockJpaRepository extends JpaRepository<UserStock, Long> {
    Optional<UserStock> findByUserAndStock(User user, Stocks stock);
}