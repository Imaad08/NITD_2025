package com.nighthawk.spring_portfolio.mvc.stocks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserStock {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Reference to the user who owns the stock

    @ManyToOne
    @JoinColumn(name = "stock_id", nullable = false)
    private Stocks stock; // Reference to the stock

    private int quantity;  // How many of this stock the user owns
    private double purchasePrice;  // Price at which the user bought the stock
}