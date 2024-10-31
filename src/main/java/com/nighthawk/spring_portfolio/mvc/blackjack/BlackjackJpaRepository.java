package com.nighthawk.spring_portfolio.mvc.blackjack;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BlackjackJpaRepository extends JpaRepository<Blackjack, Long> {
    Optional<Blackjack> findByPlayerId(Long playerId);
}