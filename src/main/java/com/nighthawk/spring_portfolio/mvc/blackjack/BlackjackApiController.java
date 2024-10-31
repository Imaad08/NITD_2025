package com.nighthawk.spring_portfolio.mvc.blackjack;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;

@RestController
@RequestMapping("/api/casino/blackjack")
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
// fix game id stuff?
    @PostMapping("/hit")
    public ResponseEntity<Blackjack> hit(@RequestBody Map<String, Object> request) {
        Long playerId = Long.parseLong(request.get("playerId").toString());
        Blackjack game = repository.findByPlayerId(playerId).orElse(null);
        if (game != null) {
            // Add hit logic here
            repository.save(game);
            return new ResponseEntity<>(game, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/stand")
    public ResponseEntity<Blackjack> stand(@RequestBody Map<String, Object> request) {
        Long playerId = Long.parseLong(request.get("playerId").toString());
        Blackjack game = repository.findByPlayerId(playerId).orElse(null);
        if (game != null) {
            // Add stand logic here
            repository.save(game);
            return new ResponseEntity<>(game, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}