package com.nighthawk.spring_portfolio.mvc.blackjack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nighthawk.spring_portfolio.mvc.user.User;
import com.nighthawk.spring_portfolio.mvc.user.UserJpaRepository;

@RestController
@RequestMapping("/api/casino/blackjack")
public class BlackjackApiController {

    private static final Logger LOGGER = Logger.getLogger(BlackjackApiController.class.getName());

    @Autowired
    private BlackjackJpaRepository repository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    // Helper method to return a JSON response
    private ResponseEntity<Object> jsonResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/start")
    public ResponseEntity<Blackjack> startGame(@RequestBody Map<String, Object> request) {
        try {
            String username = request.get("username").toString();
            double betAmount = Double.parseDouble(request.get("betAmount").toString());

            Optional<User> optionalUser = userJpaRepository.findByUsername(username);
            User user = optionalUser.orElseGet(() -> {
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setBalance(1000);
                return userJpaRepository.save(newUser);
            });

            Blackjack game = new Blackjack();
            game.setUser(user);
            game.setStatus("ACTIVE");
            game.setBetAmount(betAmount);
            game.initializeDeck();
            game.dealInitialHands();

            // Log initial state for debugging
            Map<String, Object> gameState = game.getGameStateMap();
            List<String> playerHand = (List<String>) gameState.get("playerHand");
            List<String> dealerHand = (List<String>) gameState.get("dealerHand");
            int playerScore = (int) gameState.get("playerScore");
            int dealerScore = (int) gameState.get("dealerScore");

            LOGGER.info(String.format("Game started for user: %s", username));
            LOGGER.info(String.format("Player hand: %s (Score: %d)", playerHand, playerScore));
            LOGGER.info(String.format("Dealer hand: %s (Score: %d)", dealerHand, dealerScore));

            repository.save(game);
            return new ResponseEntity<>(game, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error starting game", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // Ensure return type matches
        }
    }

    @PostMapping("/hit")
    public ResponseEntity<Object> hit(@RequestBody Map<String, Object> request) {
        try {
            String username = request.get("username").toString();
            Optional<User> optionalUser = userJpaRepository.findByUsername(username);
    
            if (optionalUser.isEmpty()) {
                return jsonResponse("User not found");
            }
    
            User user = optionalUser.get();
            Blackjack game = repository.findFirstByUserAndStatusOrderByIdDesc(user, "ACTIVE").orElse(null);
    
            if (game == null) {
                return jsonResponse("No active game found for user");
            }
    
            game.loadGameState(); // Load game state to ensure deck is populated
            Map<String, Object> gameState = game.getGameStateMap();
    
            @SuppressWarnings("unchecked")
            List<String> playerHand = (List<String>) gameState.get("playerHand");
            @SuppressWarnings("unchecked")
            List<String> deck = (List<String>) gameState.get("deck");
    
            if (deck == null || deck.isEmpty()) {
                return jsonResponse("Game over: No cards left in deck");
            }
    
            String drawnCard = deck.remove(0);
            playerHand.add(drawnCard);
            int playerScore = game.calculateScore(playerHand);
    
            gameState.put("playerHand", playerHand);
            gameState.put("deck", deck);
            gameState.put("playerScore", playerScore);
    
            if (playerScore > 21) { // Player busts
                double betAmount = game.getBetAmount();
                user.setBalance(user.getBalance() - betAmount); // Deduct the bet amount from balance
                game.setStatus("INACTIVE"); // Mark game as inactive
                gameState.put("result", "LOSE"); // Set result as lose
                game.setGameStateMap(gameState); // Persist the updated game state
                repository.save(game); // Save game state
                userJpaRepository.save(user); // Save user balance update
                return jsonResponse("Player busts! Game over.");
            }
    
            game.setGameStateMap(gameState);  // Persist the updated game state
            repository.save(game);
    
            return new ResponseEntity<>(game, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing hit", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonResponse("Internal server error"));
        }
    }
    
    @PostMapping("/stand")
    public ResponseEntity<Object> stand(@RequestBody Map<String, Object> request) {
        try {
            String username = request.get("username").toString();
            Optional<User> optionalUser = userJpaRepository.findByUsername(username);

            if (optionalUser.isEmpty()) {
                return jsonResponse("User not found");
            }

            User user = optionalUser.get();
            Blackjack game = repository.findFirstByUserAndStatusOrderByIdDesc(user, "ACTIVE").orElse(null);

            if (game == null) {
                return jsonResponse("No active game found for user");
            }

            game.loadGameState();
            Map<String, Object> gameState = game.getGameStateMap();

            @SuppressWarnings("unchecked")
            List<String> dealerHand = (List<String>) gameState.get("dealerHand");
            @SuppressWarnings("unchecked")
            List<String> deck = (List<String>) gameState.get("deck");

            int playerScore = (int) gameState.get("playerScore");
            int dealerScore = (int) gameState.get("dealerScore");
            double betAmount = game.getBetAmount();

            // Dealer's turn: Dealer hits until reaching at least 17
            while (dealerScore < 17 && !deck.isEmpty()) {
                String drawnCard = deck.remove(0);
                dealerHand.add(drawnCard);
                dealerScore = game.calculateScore(dealerHand);
            }

            gameState.put("dealerScore", dealerScore);
            gameState.put("dealerHand", dealerHand);
            gameState.put("deck", deck);

            // Determine outcome and update balance
            String result;
            if (playerScore > 21) {
                result = "LOSE";
                user.setBalance(user.getBalance() - betAmount);
            } else if (dealerScore > 21 || playerScore > dealerScore) {
                result = "WIN";
                user.setBalance(user.getBalance() + betAmount);
            } else if (playerScore < dealerScore) {
                result = "LOSE";
                user.setBalance(user.getBalance() - betAmount);
            } else {
                result = "DRAW"; // No balance change on draw
            }

            gameState.put("result", result);
            game.setStatus("INACTIVE");
            game.setGameStateMap(gameState);
            repository.save(game);
            userJpaRepository.save(user); // Ensure balance is saved

            return new ResponseEntity<>(game, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing stand", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonResponse("Internal server error"));
        }
    }
    }