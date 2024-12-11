package com.nighthawk.spring_portfolio.mvc.userStocks;

import java.util.ArrayList;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nighthawk.spring_portfolio.mvc.person.Person;
import static com.nighthawk.spring_portfolio.mvc.person.Person.startingBalance;

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

@Data // Lombok annotation to generate boilerplate code (getters, setters, toString, etc.)
@NoArgsConstructor // Lombok annotation to generate a no-argument constructor
@AllArgsConstructor // Lombok annotation to generate an all-arguments constructor
@Entity // Marks this class as a JPA entity (mapped to a database table)
@Getter // Lombok annotation to generate getters
@Setter // Lombok annotation to generate setters
public class userStocksTable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; // Primary key for the userStocksTable entity

    @Column
    private String person_name; // Name of the associated person

    @OneToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Person person; // One-to-One relationship with the Person entity

    @Column
    private String stonks; // Stock ticker symbols (e.g., AAPL, TSLA)

    @Column
    private String crypto; // Cryptocurrency ticker symbols (e.g., BTC, ETH)

    @Column
    private String balance; // Balance for the associated person

    // Constructor for initializing a userStocksTable object
    public userStocksTable(String stonks, String crypto, String balance, Person person) {
        this.person_name = person.getName();
        this.stonks = stonks;
        this.crypto = crypto;
        this.balance = balance;
        this.person = person;
    }

    // Method to initialize an array of userStocksTable objects for a list of Person entities
    public static userStocksTable[] init(Person[] persons) {
        ArrayList<userStocksTable> stocks = new ArrayList<>();
        for (Person person : persons) {
            stocks.add(new userStocksTable("AAPL,TSLA,AMZN", "BTC,ETH", startingBalance, person));
        }
        return stocks.toArray(new userStocksTable[0]);
    }

    @PreUpdate
    public void updatePersonName() {
        if (person != null) {
            this.person_name = person.getName();
        }
    }
}
