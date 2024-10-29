package com.nighthawk.spring_portfolio.mvc.poker;

import java.util.List;

public class GameResult {
    private List<Card> playerHand;
    private List<Card> dealerHand;
    private int bet;

    public GameResult(List<Card> playerHand, List<Card> dealerHand, int bet) {
        this.playerHand = playerHand;
        this.dealerHand = dealerHand;
        this.bet = bet;
    }

    // Getters
    public List<Card> getPlayerHand() {
        return playerHand;
    }

    public List<Card> getDealerHand() {
        return dealerHand;
    }

    public int getBet() {
        return bet;
    }

    @Override
    public String toString() {
        return "GameResult{" +
                "playerHand=" + playerHand +
                ", dealerHand=" + dealerHand +
                ", bet=" + bet +
                '}';
    }
}
