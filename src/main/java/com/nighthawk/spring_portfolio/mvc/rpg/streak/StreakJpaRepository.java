package com.nighthawk.spring_portfolio.mvc.rpg.streak;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StreakJpaRepository extends JpaRepository<Streak, Long> {
    List<Streak> findByUserId(Long userId);
    Optional<Streak> findByUserIdAndMaxStreak(Long userId, int maxStreak);
}
