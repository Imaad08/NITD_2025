package com.nighthawk.spring_portfolio.mvc.poker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PokerJpaRepository extends JpaRepository<Card, Long> {
    // Define custom database query methods if needed
}
