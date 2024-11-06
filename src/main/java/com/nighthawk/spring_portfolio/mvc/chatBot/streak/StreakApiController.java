package com.nighthawk.spring_portfolio.mvc.rpg.streak;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;

@RestController
@RequestMapping("/rpg_streak")
public class StreakApiController {

    @Autowired
    private StreakJpaRepository streakJpaRepository;

    @GetMapping("/streak/{id}")
    public ResponseEntity<Streak> getStreak(@PathVariable long id) {
        Optional<Streak> optional = streakJpaRepository.findById(id);
        if (optional.isPresent()) {
            Streak streak = optional.get();
            return new ResponseEntity<>(streak, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/streak")
    public ResponseEntity<List<Streak>> getStreak() {
        return new ResponseEntity<>(streakJpaRepository.findAll(), HttpStatus.OK);
    }

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
        private String lastInteractionDate;
        private String lastResetDate;
    }

    @PostMapping("/streak")
    public ResponseEntity<Object> postStreak(@RequestBody StreakDto streakDto) {
        Streak streak = new Streak(streakDto.getUserId(), streakDto.getCurrentStreak(), streakDto.getMaxStreak(), streakDto.getLastInteractionDate(), streakDto.getLastResetDate());
        streakJpaRepository.save(streak);
        return new ResponseEntity<>("Streak created successfully", HttpStatus.CREATED);
    }

    @PostMapping("/streak/search")
    public ResponseEntity<Object> streakSearch(@RequestBody final Map<String, String> map) {
        String term = map.get("term");
        if (term == null || term.isEmpty()) {
            return new ResponseEntity<>("Term is missing or empty", HttpStatus.BAD_REQUEST);
        }
        try {
            Long userId = Long.parseLong(term);
            List<Streak> listByUserId = streakJpaRepository.findByUserId(userId);
            return new ResponseEntity<>(listByUserId, HttpStatus.OK);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>("Term must be a valid number", HttpStatus.BAD_REQUEST);
        }
    }
}
