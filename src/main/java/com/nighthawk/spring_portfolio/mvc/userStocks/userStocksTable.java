package com.nighthawk.spring_portfolio.mvc.userStocks;

import java.util.ArrayList;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nighthawk.spring_portfolio.mvc.person.Person;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreUpdate;
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
    private Long id; // Primary key

    @Column
    private String person_name;

    @OneToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id") // Maps to the Person table's primary key
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Person person; // One-to-One relationship with Person entity

    private String stonks; // Stock ticker
    private String crypto; // Cryptocurrency ticker

    // Constructor for initializing user stocks
    public userStocksTable(String stonks, String crypto, Person person) {
        this.person_name = person.getName();
        this.stonks = stonks;
        this.crypto = crypto;
        this.person = person;
    }

    // Initialization method to create userStocksTable objects for a list of persons
    public static userStocksTable[] init(Person[] persons) {
        ArrayList<userStocksTable> stocks = new ArrayList<>();
        for (Person person : persons) {
            stocks.add(new userStocksTable("AAPL,TSLA,AMZN", "BTC,ETH", person));
        }
        return stocks.toArray(new userStocksTable[0]);
    }
    @PreUpdate
    public void updatePersonName() {
        if (person != null) {
            this.person_name = person.getName(); // Update person_name with the latest name
        }
    }
}
