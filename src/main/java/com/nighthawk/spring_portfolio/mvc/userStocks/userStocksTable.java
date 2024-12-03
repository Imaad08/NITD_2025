package com.nighthawk.spring_portfolio.mvc.userStocks;

import com.nighthawk.spring_portfolio.mvc.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class userStocksTable {
    
    @Id
    private Integer id; // Shared primary key with User table

    @Column(unique = true, nullable = false)
    private String username;

    private String stonks;
    private String crypto;
    
    @OneToOne
    @JoinColumn(name = "user_id") // Maps to the User table's primary key
    private User user; // Relationship with User entity
}