package com.nighthawk.spring_portfolio.mvc.poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PokerBoard {
    private List<Card> deck;
    private List<Card> playerHand;
    private List<Card> dealerHand;

    public PokerBoard() {
        deck = new ArrayList<>();
        initializeDeck();
        shuffleDeck();
        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();
    }

    // Initialize deck with 52 cards
    private void initializeDeck() {
        String[] suits = {"♠", "♣", "♥", "♦"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        for (String suit : suits) {
            for (String rank : ranks) {
                deck.add(new Card(rank, suit));
            }
        }
    }

    // Shuffle the deck
    private void shuffleDeck() {
        Collections.shuffle(deck, new Random());
    }

    // Deal a hand to both player and dealer
    public void dealHands() {
        playerHand.clear();
        dealerHand.clear();

        for (int i = 0; i < 5; i++) {
            playerHand.add(deck.remove(0));
            dealerHand.add(deck.remove(0));
        }
    }

    public List<Card> getPlayerHand() {
        return playerHand;
    }

    public List<Card> getDealerHand() {
        return dealerHand;
    }
}
