package com.nighthawk.spring_portfolio.mvc.stocks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

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

    private double currentPrice; // Current stock price
    private int ownedQuantity;   // Number of stocks owned by the user

    @Column(name = "buy_price")
    private double buyPrice;     // Price at which the stock was bought

    // Initialize some sample stocks for testing
    public static Stocks[] init() {
        return new Stocks[] {
            new Stocks(null, "AAPL", 175.0, 10, 160.0),
            new Stocks(null, "TSLA", 800.0, 5, 700.0),
            new Stocks(null, "GOOGL", 2800.0, 2, 2600.0),
        };
    }
}