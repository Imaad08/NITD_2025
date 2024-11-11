package com.nighthawk.spring_portfolio.mvc.blackjack;

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

import com.nighthawk.spring_portfolio.mvc.stocks.User;
import com.nighthawk.spring_portfolio.mvc.stocks.UserJpaRepository;

@RestController
@RequestMapping("/api/casino/blackjack")
public class BlackjackApiController {

    private static final Logger LOGGER = Logger.getLogger(BlackjackApiController.class.getName());

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

            LOGGER.info("Game started for user: " + username);
            LOGGER.info("Player hand: " + playerHand + " (Score: " + playerScore + ")");
            LOGGER.info("Dealer hand: " + dealerHand + " (Score: " + dealerScore + ")");

            repository.save(game);
            return new ResponseEntity<>(game, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error starting game", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping("/hit")
    public ResponseEntity<Object> hit(@RequestBody Map<String, Object> request) {
        try {
            String username = request.get("username").toString();
            Optional<User> optionalUser = userJpaRepository.findByUsername(username);
    
            if (optionalUser.isEmpty()) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
    
            User user = optionalUser.get();
            Blackjack game = repository.findFirstByUserAndStatusOrderByIdDesc(user, "ACTIVE").orElse(null);
    
            if (game == null) {
                return new ResponseEntity<>("No active game found for user", HttpStatus.NOT_FOUND);
            }
    
            game.loadGameState(); // Load game state to ensure deck is populated
            Map<String, Object> gameState = game.getGameStateMap();
            
            @SuppressWarnings("unchecked")
            List<String> playerHand = (List<String>) gameState.get("playerHand");
            @SuppressWarnings("unchecked")
            List<String> deck = (List<String>) gameState.get("deck");
    
            if (deck == null || deck.isEmpty()) {
                return new ResponseEntity<>("Game over: No cards left in deck", HttpStatus.BAD_REQUEST);
            }
    
            // Check if player already busted
            int playerScore = gameState.get("playerScore") != null ? ((Number) gameState.get("playerScore")).intValue() : 0;
            if (playerScore > 21) {
                return new ResponseEntity<>("Player already busted", HttpStatus.BAD_REQUEST);
            }
    
            // Draw a card for the player and calculate the new score
            String drawnCard = deck.remove(0);
            playerHand.add(drawnCard);
            playerScore = game.calculateScore(playerHand);
    
            gameState.put("playerHand", playerHand);
            gameState.put("deck", deck);
            gameState.put("playerScore", playerScore);
    
            // Check if player busts after this hit
            if (playerScore > 21) {
                gameState.put("result", "LOSE");
                game.setStatus("INACTIVE");
                user.setBalance(user.getBalance() - game.getBetAmount());
                game.setGameStateMap(gameState); // Persist updated game state and result
                repository.save(game);
                userJpaRepository.save(user); // Update user balance
                return new ResponseEntity<>("Player busts! Game over.", HttpStatus.OK);
            }
    
            // Save game state if the player has not busted
            game.setGameStateMap(gameState); // Persist updated game state
            repository.save(game);
    
            return new ResponseEntity<>(game, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing hit", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    

    @PostMapping("/stand")
    public ResponseEntity<Object> stand(@RequestBody Map<String, Object> request) {
        try {
            String username = request.get("username").toString();
            Optional<User> optionalUser = userJpaRepository.findByUsername(username);

            if (optionalUser.isEmpty()) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            User user = optionalUser.get();
            Blackjack game = repository.findFirstByUserAndStatusOrderByIdDesc(user, "ACTIVE").orElse(null);

            if (game == null) {
                return new ResponseEntity<>("No active game found for user", HttpStatus.NOT_FOUND);
            }

            game.loadGameState(); // Load game state to ensure deck and hands are populated
            Map<String, Object> gameState = game.getGameStateMap();

            @SuppressWarnings("unchecked")
            List<String> dealerHand = (List<String>) gameState.get("dealerHand");
            @SuppressWarnings("unchecked")
            List<String> deck = (List<String>) gameState.get("deck");

            int playerScore = (int) gameState.get("playerScore");
            int dealerScore = (int) gameState.get("dealerScore");

            // Dealer's turn: Reveal hidden card and draw until score is at least 17
            while (dealerScore < 17 && !deck.isEmpty()) {
                String drawnCard = deck.remove(0);
                dealerHand.add(drawnCard);
                dealerScore = game.calculateScore(dealerHand);
            }

            // Update game state with dealer's final score
            gameState.put("dealerScore", dealerScore);
            gameState.put("dealerHand", dealerHand);
            gameState.put("deck", deck);

            // Determine outcome
            String result;
            double betAmount = game.getBetAmount();

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
                result = "DRAW"; // In case of a tie, no balance change
            }

            gameState.put("result", result);
            game.setStatus("INACTIVE");
            game.setGameStateMap(gameState);
            repository.save(game);
            userJpaRepository.save(user); // Save the updated user balance

            return new ResponseEntity<>(game, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing stand", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    }