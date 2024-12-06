package com.nighthawk.spring_portfolio.mvc.rpg.answer;

public class LeaderboardDto {
    private Long id;
    private String userName;
    private Long totalScore;

    public LeaderboardDto(Long id, Long totalScore) {
        this.id = id;
        this.totalScore = totalScore;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getuserName() {
        return userName;
    }

    public void setuserName(String userName) {
        this.userName = userName;
    }

    public Long getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Long totalScore) {
        this.totalScore = totalScore;
    }
}