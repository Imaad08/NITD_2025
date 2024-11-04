package com.nighthawk.spring_portfolio.mvc.stocks;

import java.util.Random;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Stocks {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique=true)
    private String stockSymbol; // e.g., AAPL, TSLA

    private double currentPrice; // Current price of the stock

    // Simulate price changes dynamically
    public void fluctuatePrice() {
        Random random = new Random();
        double change = (random.nextDouble() * 10) - 5; // Prices fluctuate randomly between -5 to +5
        this.currentPrice = Math.max(0, this.currentPrice + change); // Price cannot be negative
    }
}