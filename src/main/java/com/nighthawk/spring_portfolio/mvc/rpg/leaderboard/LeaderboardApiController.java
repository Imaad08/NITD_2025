package com.nighthawk.spring_portfolio.mvc.rpg.leaderboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardApiController {

    @Autowired
    private LeaderboardJpaRepository repository;

    // Endpoint to get the entire leaderboard sorted by score
    @GetMapping
    public List<Leaderboard> getLeaderboard() {
        return repository.findAllByOrderByScoreDesc();
    }

    // Endpoint to add a new score entry
    @PostMapping
    public Leaderboard addScore(@RequestBody Leaderboard leaderboard) {
        return repository.save(leaderboard);
    }

    // Endpoint to reset the leaderboard (for demonstration)
    @DeleteMapping("/reset")
    public void resetLeaderboard() {
        repository.deleteAll();
    }
}
