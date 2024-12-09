package com.nighthawk.spring_portfolio.mvc.userStocks;

import java.util.ArrayList;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.nighthawk.spring_portfolio.mvc.person.Person;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; // Shared primary key with User table

    @Column
    private String person_name;
    
    @ManyToOne
    @JoinColumn(name = "person_id") // Maps to the User table's primary key
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Person person; // Relationship with User entity
    private String stonks;
    private String crypto;

    public userStocksTable(String stonks, String crypto, Person person)
    {
        this.person_name = person.getName();
        this.stonks = stonks;
        this.crypto = crypto;
        this.person = person;
    }
    public static userStocksTable[] init(Person[] persons)
    {
        ArrayList<userStocksTable> stocks = new ArrayList<>();
        for(Person person: persons)
        {
            stocks.add(new userStocksTable("AAPL", "BTC", person));
        }
        return stocks.toArray(new userStocksTable[0]);
    }
}