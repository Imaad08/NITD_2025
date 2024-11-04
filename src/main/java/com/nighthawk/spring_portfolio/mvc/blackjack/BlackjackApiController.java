package com.nighthawk.spring_portfolio.mvc.blackjack;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(BlackjackApiController.class);

    @PostMapping("/start")
    public ResponseEntity<Blackjack> startGame(@RequestBody Map<String, Object> request) {
        try {
            String email = request.get("email").toString();
            String password = request.get("password").toString();
            
            Person person = personRepository.findByEmail(email);
            
            if (person == null) {
                logger.error("Person not found with email: " + email);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            int betAmount = request.containsKey("betAmount") 
                ? Integer.parseInt(request.get("betAmount").toString()) 
                : 10;

            Blackjack game = new Blackjack(person, betAmount);
            repository.save(game);
            return new ResponseEntity<>(game, HttpStatus.OK);
        } catch (NumberFormatException e) {
            logger.error("Invalid bet amount", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error starting game", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/hit")
    public ResponseEntity<Blackjack> hit(@RequestBody Map<String, Object> request) {
        try {
            String email = request.get("email").toString();
            Person person = personRepository.findByEmail(email);
            
            if (person == null) {
                logger.error("Person not found with email: " + email);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Blackjack game = repository.findFirstByPersonAndStatusOrderByIdDesc(person, "ACTIVE").orElse(null);
            
            if (game == null) {
                logger.error("No ongoing game for person: " + email);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            @SuppressWarnings("unchecked")
            List<String> playerHand = (List<String>) game.getGameState().get("playerHand");
            @SuppressWarnings("unchecked")
            List<String> deck = (List<String>) game.getGameState().get("deck");

            if (!deck.isEmpty()) {
                String drawnCard = deck.remove(0);
                playerHand.add(drawnCard);

                game.getGameState().put("playerHand", playerHand);
                game.getGameState().put("deck", deck);
                game.getGameState().put("playerScore", calculateScore(playerHand));

                repository.save(game);
                logger.info("Player hit: " + drawnCard + " added to hand.");
                return new ResponseEntity<>(game, HttpStatus.OK);
            }
            logger.error("Deck is empty");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error processing hit request", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/stand")
    public ResponseEntity<Blackjack> stand(@RequestBody Map<String, Object> request) {
        try {
            String email = request.get("email").toString();
            Person person = personRepository.findByEmail(email);
            
            if (person == null) {
                logger.error("Person not found with email: " + email);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Blackjack game = repository.findFirstByPersonAndStatusOrderByIdDesc(person, "ACTIVE").orElse(null);
            if (game == null) {
                logger.error("No ongoing game for person: " + email);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Mark game as inactive
            game.setStatus("INACTIVE");
            repository.save(game);
            logger.info("Player stands and game marked as inactive.");
            return new ResponseEntity<>(game, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error processing stand request", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private int calculateScore(List<String> hand) {
        int score = 0;
        int aceCount = 0;

        for (String card : hand) {
            String rank = card.split(" ")[0];
            switch (rank) {
                case "A" -> {
                    aceCount++;
                    score += 11;
                }
                case "K", "Q", "J" -> score += 10;
                default -> score += Integer.parseInt(rank);
            }
        }

        while (score > 21 && aceCount > 0) {
            score -= 10;
            aceCount--;
        }

        return score;
    }
}