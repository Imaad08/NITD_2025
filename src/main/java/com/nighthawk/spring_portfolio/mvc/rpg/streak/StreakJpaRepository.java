package com.nighthawk.spring_portfolio.mvc.rpg.streak;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StreakJpaRepository extends JpaRepository<Streak, Long> {

    // Method to find a single streak by userId
    Optional<Streak> findByUserId(Long userId);

    // Method to find streaks by userId or maxStreak (if needed for more complex queries)
    List<Streak> findByUserIdOrMaxStreak(Long userId, int maxStreak);
}
