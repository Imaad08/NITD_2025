package com.nighthawk.spring_portfolio.mvc.stocks;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserStockJpaRepository extends JpaRepository<UserStock, Long> {
    Optional<UserStock> findByUserAndStock(User user, Stocks stock);
    @Query("SELECT us FROM UserStock us WHERE us.user.username = :username")
    Optional<UserStock> findByUsername(@Param("username") String username);
}