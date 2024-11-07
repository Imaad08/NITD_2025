package com.nighthawk.spring_portfolio.mvc.rpg.answer;

public class LeaderboardDto {
    private Long id;
    private String playerName;
    private Long totalScore;

    // Constructor matching the query result
    public LeaderboardDto(Long id, Long totalScore) {
        this.id = id;
        this.totalScore = totalScore;
    }

    // Getters and setters remain the same

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Long getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Long totalScore) {
        this.totalScore = totalScore;
    }
}