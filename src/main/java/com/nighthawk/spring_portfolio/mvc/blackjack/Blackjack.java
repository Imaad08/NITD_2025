package com.nighthawk.spring_portfolio.mvc.blackjack;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nighthawk.spring_portfolio.mvc.stocks.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class Blackjack {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String status;

    @Column(columnDefinition = "TEXT")
    private String gameState;

    @Column(nullable = false)  // Ensures betAmount cannot be null in the database
    private double betAmount;  // New field for storing the bet amount

    @Transient
    private Map<String, Object> gameStateMap = new HashMap<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
        this.gameStateMap = fromJsonString(gameState);
    }

    public double getBetAmount() {  // Getter for betAmount
        return betAmount;
    }

    public void setBetAmount(double betAmount) {  // Setter for betAmount
        this.betAmount = betAmount;
    }

    public Map<String, Object> getGameStateMap() {
        return gameStateMap;
    }

    public void setGameStateMap(Map<String, Object> gameStateMap) {
        this.gameStateMap = gameStateMap;
        this.gameState = toJsonString(gameStateMap);
    }

    private String toJsonString(Map<String, Object> map) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert map to JSON string", e);
        }
    }

    private Map<String, Object> fromJsonString(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, HashMap.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert JSON string to map", e);
        }
    }
}
