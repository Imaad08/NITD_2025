package com.nighthawk.spring_portfolio.mvc.user;

import com.nighthawk.spring_portfolio.mvc.userStocks.userStocksTable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role = "USER";
    private boolean enabled = true;
    public double balance;
    private String stonks;

    @OneToOne
    @JoinColumn(name = "finance_id")
    private userStocksTable userStocksTable; // Reverse mapping
}