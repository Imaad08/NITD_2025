package com.nighthawk.spring_portfolio.mvc.blackjack;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Blackjack {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    private Long playerId;  // References Person entity

    private int betAmount;
    private String gameStatus; // "IN_PROGRESS", "PLAYER_WON", "DEALER_WON", "PUSH"

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> gameState = new HashMap<>();  // Stores cards, scores, etc.

    public Blackjack(Long playerId, int betAmount) {
        this.playerId = playerId;
        this.betAmount = betAmount;
        this.gameStatus = "IN_PROGRESS";
        initializeGame();
    }

    private void initializeGame() {
        List<String> playerHand = new ArrayList<>();
        List<String> dealerHand = new ArrayList<>();
        
        gameState.put("playerHand", playerHand);
        gameState.put("dealerHand", dealerHand);
        gameState.put("playerScore", 0);
        gameState.put("dealerScore", 0);
        gameState.put("deck", initializeDeck());
    }

    private List<String> initializeDeck() {
        List<String> deck = new ArrayList<>();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        
        for (String suit : suits) {
            for (String rank : ranks) {
                deck.add(rank + " of " + suit);
            }
        }
        return deck;
    }
}