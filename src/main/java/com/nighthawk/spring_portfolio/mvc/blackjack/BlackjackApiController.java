package com.nighthawk.spring_portfolio.mvc.blackjack;

import java.util.List;
import java.util.Map;  // Added this import

import org.springframework.beans.factory.annotation.Autowired;  // Added this import
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
            // Find person by email
            String email = request.get("email").toString();
            Person person = personRepository.findByEmail(email);
            
            if (person == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Verify password (basic authentication)
            String password = request.get("password").toString();
            if (!person.getPassword().equals(password)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            int betAmount = request.containsKey("betAmount") 
                ? Integer.parseInt(request.get("betAmount").toString()) 
                : 10;

            // Create new game with Person object
            Blackjack game = new Blackjack(person, betAmount);
            repository.save(game);
            return new ResponseEntity<>(game, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/hit")
    public ResponseEntity<Blackjack> hit(@RequestBody Map<String, Object> request) {
        try {
            String email = request.get("email").toString();
            Person person = personRepository.findByEmail(email);
            
            if (person == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Find game by person instead of playerId
            Blackjack game = repository.findByPerson(person).orElse(null);
            
            if (game == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Add hit logic here
            @SuppressWarnings("unchecked")  // Added to suppress unchecked cast warning
            List<String> playerHand = (List<String>) game.getGameState().get("playerHand");
            @SuppressWarnings("unchecked")  // Added to suppress unchecked cast warning
            List<String> deck = (List<String>) game.getGameState().get("deck");

            if (!deck.isEmpty()) {
                String drawnCard = deck.remove(0);
                playerHand.add(drawnCard);

                game.getGameState().put("playerHand", playerHand);
                game.getGameState().put("deck", deck);
                game.getGameState().put("playerScore", calculateScore(playerHand));

                repository.save(game);
                return new ResponseEntity<>(game, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/stand")
    public ResponseEntity<Blackjack> stand(@RequestBody Map<String, Object> request) {
        try {
            String email = request.get("email").toString();
            Person person = personRepository.findByEmail(email);
            
            if (person == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Blackjack game = repository.findByPerson(person).orElse(null);
            if (game != null) {
                // Add stand logic here
                repository.save(game);
                return new ResponseEntity<>(game, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
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