package com.nighthawk.spring_portfolio.mvc.stocks;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

public class UserStocks {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    private String user_id; // Reference to the user who owns the stock

    
    @Column(nullable = false)
    private String stonks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser() {
        return user_id;
    }

    public void setUser(String user_id) {
        this.user_id = user_id;
    }

    public String getStonks() {
        return stonks;
    }

    public void setStonks(String stonks) {
        this.stonks = stonks;
    }
}
