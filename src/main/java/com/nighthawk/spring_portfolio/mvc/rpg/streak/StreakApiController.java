package com.nighthawk.spring_portfolio.mvc.rpg.streak;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;

@RestController
@RequestMapping("/rpg_streak")
@CrossOrigin(origins = "http://localhost:5500") // Adjust this based on your frontend origin if needed
public class StreakApiController {

    @Autowired
    private StreakJpaRepository streakJpaRepository;

    // Get streak by ID
    @GetMapping("/streak/{id}")
    public ResponseEntity<Streak> getStreak(@PathVariable long id) {
        Optional<Streak> optional = streakJpaRepository.findById(id);
        return optional.map(streak -> new ResponseEntity<>(streak, HttpStatus.OK))
                       .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Get streak by user ID
    @GetMapping("/streak")
    public ResponseEntity<Streak> getStreakByUserId(@RequestParam Long userId) {
        Optional<Streak> streak = streakJpaRepository.findByUserId(userId);
        return streak.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // Delete streak by ID
    @DeleteMapping("/streak/{id}")
    public ResponseEntity<Streak> deleteStreak(@PathVariable long id) {
        Optional<Streak> optional = streakJpaRepository.findById(id);
        if (optional.isPresent()) {
            Streak streak = optional.get();
            streakJpaRepository.deleteById(id);
            return new ResponseEntity<>(streak, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Getter
    public static class StreakDto {
        private Long userId;
        private int currentStreak;
        private int maxStreak;
    }

    // Post a new streak or update an existing one
    @PostMapping("/streak")
    public ResponseEntity<Object> postStreak(@RequestBody StreakDto streakDto) {
        Optional<Streak> existingStreak = streakJpaRepository.findByUserId(streakDto.getUserId());
        Streak streak;
        
        if (existingStreak.isPresent()) {
            // Update existing streak
            streak = existingStreak.get();
            streak.setCurrentStreak(streak.getCurrentStreak() + 1); // Increment the current streak
            streak.setMaxStreak(Math.max(streak.getMaxStreak(), streak.getCurrentStreak())); // Update max streak if necessary
        } else {
            // Create new streak if none exists
            streak = new Streak(
                streakDto.getUserId(),
                streakDto.getCurrentStreak(),
                streakDto.getMaxStreak()
            );
        }

        streakJpaRepository.save(streak); // Save either the new or updated streak
        return new ResponseEntity<>(streak, HttpStatus.CREATED);
    }

    // Search for streaks by userId or maxStreak
    @PostMapping("/streak/search")
    public ResponseEntity<Object> streakSearch(@RequestBody final Map<String, String> map) {
        String term = map.get("term");
        List<Streak> list = streakJpaRepository.findByUserIdOrMaxStreak(
            Long.parseLong(term), 
            Integer.parseInt(term)
        );
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
