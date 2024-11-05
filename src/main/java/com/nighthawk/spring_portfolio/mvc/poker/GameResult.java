package com.nighthawk.spring_portfolio.mvc.poker;

import java.util.List;

public class GameResult {
    private List<Card> playerHand;
    private List<Card> dealerHand;
    private double bet;  // Change to double

    public GameResult(List<Card> playerHand, List<Card> dealerHand, double bet) {  // Update parameter type
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

    public double getBet() {  // Update return type
        return bet;
    }

    public boolean isPlayerWin() {
        return calculateHandValue(playerHand) > calculateHandValue(dealerHand);
    }

    public boolean isDealerWin() {
        return calculateHandValue(dealerHand) > calculateHandValue(playerHand);
    }

    public double getWinnings() {
        return bet * 2;
    }

    private int calculateHandValue(List<Card> hand) {
        int value = 0;
        for (Card card : hand) {
            value += getCardValue(card);
        }
        return value;
    }

    private int getCardValue(Card card) {
        switch (card.getRank()) {
            case "A": return 14;
            case "K": return 13;
            case "Q": return 12;
            case "J": return 11;
            default: return Integer.parseInt(card.getRank());
        }
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
