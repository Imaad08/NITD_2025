package com.nighthawk.spring_portfolio.mvc.rpg.leaderboard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaderboardJpaRepository extends JpaRepository<Leaderboard, Long> {

    // Optional: Define custom queries if needed
    List<Leaderboard> findAllByOrderByScoreDesc(); // To get the leaderboard ordered by score descending
}
