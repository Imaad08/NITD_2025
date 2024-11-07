package com.nighthawk.spring_portfolio.mvc.poker;

public class Card implements Comparable<Card> {
    private String rank;
    private String suit;

    // Constructor
    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

    // Getters
    public String getRank() {
        return rank;
    }

    public String getSuit() {
        return suit;
    }

    // Implement compareTo for sorting based on rank value
    @Override
    public int compareTo(Card other) {
        return Integer.compare(getCardRankValue(), other.getCardRankValue());
    }

    // Converts rank to integer value for comparison
    public int getCardRankValue() {
        switch (rank) {
            case "A": return 14;
            case "K": return 13;
            case "Q": return 12;
            case "J": return 11;
            default: return Integer.parseInt(rank);
        }
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}
