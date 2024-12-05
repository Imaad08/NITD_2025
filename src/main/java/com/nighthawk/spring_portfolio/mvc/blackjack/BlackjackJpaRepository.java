package com.nighthawk.spring_portfolio.mvc.blackjack;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nighthawk.spring_portfolio.mvc.user.User;

public interface BlackjackJpaRepository extends JpaRepository<Blackjack, Long> {
    Optional<Blackjack> findFirstByUserAndStatusOrderByIdDesc(User user, String status);
}
