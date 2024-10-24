package com.nighthawk.spring_portfolio.mvc.stocks;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StocksJpaRepository extends JpaRepository<Stocks, Long> {
    List<Stocks> findAllByOrderByStockSymbolAsc(); // Custom method to find all stocks ordered by symbol
    List<Stocks> findByStockSymbolIgnoreCase(String stockSymbol); // Find stock by symbol
}