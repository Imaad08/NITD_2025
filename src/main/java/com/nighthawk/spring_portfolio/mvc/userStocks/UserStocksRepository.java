package com.nighthawk.spring_portfolio.mvc.userStocks;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStocksRepository extends JpaRepository<userStocksTable, Long> {
    // You can add custom methods if needed, but JpaRepository already provides save(), findById(), etc.
    Optional<userStocksTable> findByPersonName(String personName);
}
