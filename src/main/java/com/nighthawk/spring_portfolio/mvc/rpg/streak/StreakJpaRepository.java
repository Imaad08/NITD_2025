package com.nighthawk.spring_portfolio.mvc.rpg.streak;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StreakJpaRepository extends JpaRepository<Streak, Long> {
    
    // Define any custom query methods if needed
    List<Streak> findByUserIdOrMaxStreak(Long userId, int maxStreak);
}