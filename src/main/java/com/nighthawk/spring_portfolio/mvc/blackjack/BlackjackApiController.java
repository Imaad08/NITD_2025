package com.nighthawk.spring_portfolio.mvc.blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nighthawk.spring_portfolio.mvc.stocks.User;
import com.nighthawk.spring_portfolio.mvc.stocks.UserJpaRepository;

@RestController
@RequestMapping("/api/casino/blackjack")
public class BlackjackApiController {

    @Autowired
    private BlackjackJpaRepository repository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @PostMapping("/start")
    public ResponseEntity<Blackjack> startGame(@RequestBody Map<String, Object> request) {
        try {
            String username = request.get("username").toString();
            double betAmount = Double.parseDouble(request.get("betAmount").toString());

            Optional<User> optionalUser = userJpaRepository.findByUsername(username);
            User user;

            if (optionalUser.isEmpty()) {
                user = new User();
                user.setUsername(username);
                user.setBalance(1000); // Set an initial balance for new users
                user = userJpaRepository.save(user);
            } else {
                user = optionalUser.get();
            }

            // Generate and shuffle the deck
            List<String> deck = generateDeck();
            Collections.shuffle(deck);

            List<String> playerHand = new ArrayList<>();
            List<String> dealerHand = new ArrayList<>();

            // Deal initial cards
            playerHand.add(deck.remove(0));
            playerHand.add(deck.remove(0));
            dealerHand.add(deck.remove(0));
            dealerHand.add(deck.remove(0));

            Map<String, Object> gameState = new HashMap<>();
            gameState.put("deck", deck);
            gameState.put("playerHand", playerHand);
            gameState.put("dealerHand", dealerHand);
            gameState.put("playerScore", calculateScore(playerHand));
            gameState.put("dealerScore", calculateScore(dealerHand));
            gameState.put("betAmount", betAmount);

            Blackjack game = new Blackjack();
            game.setUser(user);
            game.setStatus("ACTIVE");
            game.setBetAmount(betAmount);  // Set the betAmount for this game
            game.setGameStateMap(gameState);

            repository.save(game);
            return new ResponseEntity<>(game, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(null);
        }
    }

    @PostMapping("/hit")
    public ResponseEntity<Blackjack> hit(@RequestBody Map<String, Object> request) {
        try {
            String username = request.get("username").toString();
            Optional<User> optionalUser = userJpaRepository.findByUsername(username);
            
            if (optionalUser.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            User user = optionalUser.get();
            Blackjack game = repository.findFirstByUserAndStatusOrderByIdDesc(user, "ACTIVE").orElse(null);

            if (game == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Map<String, Object> gameState = game.getGameStateMap();
            List<String> playerHand = (List<String>) gameState.get("playerHand");
            List<String> deck = (List<String>) gameState.get("deck");

            // Check if deck is null or empty
            if (deck == null || deck.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Draw a card and update the game state
            String drawnCard = deck.remove(0);
            playerHand.add(drawnCard);
            gameState.put("playerHand", playerHand);
            gameState.put("deck", deck);
            gameState.put("playerScore", calculateScore(playerHand));

            game.setGameStateMap(gameState);
            repository.save(game);
            return new ResponseEntity<>(game, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/stand")
    public ResponseEntity<Blackjack> stand(@RequestBody Map<String, Object> request) {
        try {
            String username = request.get("username").toString();
            Optional<User> optionalUser = userJpaRepository.findByUsername(username);

            if (optionalUser.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            User user = optionalUser.get();
            Blackjack game = repository.findFirstByUserAndStatusOrderByIdDesc(user, "ACTIVE").orElse(null);
            
            if (game == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Map<String, Object> gameState = game.getGameStateMap();
            
            // Check for null values and set default if necessary
            int playerScore = gameState.get("playerScore") != null ? ((Number) gameState.get("playerScore")).intValue() : 0;
            int dealerScore = gameState.get("dealerScore") != null ? ((Number) gameState.get("dealerScore")).intValue() : 0;
            double betAmount = game.getBetAmount();

            // Determine game outcome
            if (playerScore > dealerScore && playerScore <= 21) {
                user.setBalance(user.getBalance() + betAmount);
                gameState.put("result", "WIN");
            } else {
                user.setBalance(user.getBalance() - betAmount);
                gameState.put("result", "LOSE");
            }

            // Update game status and save changes
            game.setStatus("INACTIVE");
            game.setGameStateMap(gameState);
            repository.save(game);
            userJpaRepository.save(user);
            return new ResponseEntity<>(game, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

    private int calculateScore(List<String> hand) {
        int score = 0;
        int aces = 0;
        for (String card : hand) {
            String rank = card.substring(0, card.length() - 1);
            switch (rank) {
                case "A":
                    aces++;
                    score += 11;
                    break;
                case "K":
                case "Q":
                case "J":
                    score += 10;
                    break;
                default:
                    score += Integer.parseInt(rank);
                    break;
            }
        }
        while (score > 21 && aces > 0) {
            score -= 10;
            aces--;
        }
        return score;
    }

    private List<String> generateDeck() {
        String[] suits = {"H", "D", "C", "S"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        List<String> deck = new ArrayList<>();
        for (String suit : suits) {
            for (String rank : ranks) {
                deck.add(rank + suit);
            }
        }
        return deck;
    }
}
