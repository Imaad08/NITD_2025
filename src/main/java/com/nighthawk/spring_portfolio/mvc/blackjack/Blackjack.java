package com.nighthawk.spring_portfolio.mvc.blackjack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.nighthawk.spring_portfolio.mvc.person.Person;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Blackjack {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;  // Changed from playerId to person

    private int betAmount;
    private String gameStatus;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> gameState = new HashMap<>();

    public Blackjack(Person person, int betAmount) {  // Updated constructor
        this.person = person;
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