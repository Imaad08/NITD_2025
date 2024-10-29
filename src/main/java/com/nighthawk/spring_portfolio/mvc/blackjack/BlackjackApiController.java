package com.nighthawk.spring_portfolio.mvc.blackjack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;

import java.util.*;

@RestController
@RequestMapping("/api/blackjack")
public class BlackjackApiController {
    @Autowired
    private BlackjackJpaRepository repository;

    @Autowired
    private PersonJpaRepository personRepository;

    @PostMapping("/start")
    public ResponseEntity<Blackjack> startGame(@RequestBody Map<String, Object> request) {
        try {
            Long playerId = Long.parseLong(request.get("playerId").toString());
            int betAmount = Integer.parseInt(request.get("betAmount").toString());

            // Verify player exists and has enough credits
            Person player = personRepository.findById(playerId).orElse(null);
            if (player == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Create new game
            Blackjack game = new Blackjack(playerId, betAmount);
            repository.save(game);
            return new ResponseEntity<>(game, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/hit/{gameId}")
    public ResponseEntity<Blackjack> hit(@PathVariable Long gameId) {
        Optional<Blackjack> optionalGame = repository.findById(gameId);
        if (optionalGame.isPresent()) {
            Blackjack game = optionalGame.get();
            // Add hit logic here
            return new ResponseEntity<>(game, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/stand/{gameId}")
    public ResponseEntity<Blackjack> stand(@PathVariable Long gameId) {
        Optional<Blackjack> optionalGame = repository.findById(gameId);
        if (optionalGame.isPresent()) {
            Blackjack game = optionalGame.get();
            // Add stand logic here
            return new ResponseEntity<>(game, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}