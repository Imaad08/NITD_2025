package com.nighthawk.spring_portfolio.mvc.blackjack;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlackjackJpaRepository extends JpaRepository<Blackjack, Long> {
    List<Blackjack> findByPlayerId(Long playerId);
}