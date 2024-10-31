package com.nighthawk.spring_portfolio.mvc.rpg.leaderboard;

import java.util.ArrayList;

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
public class Leaderboard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String playerName;

    @Column(nullable = false, unique = true)
    private int score;

    // Optional fields for additional information, like timestamp
    @Column(nullable = false, unique = true)
    private String date;

    @Column(nullable = false, unique = true)
    private int rank;

    public Leaderboard(String playerName, int rank, int score, String date) {
        this.playerName = playerName;
        this.rank = rank;
        this.score = score;
        this.date = date;
    }

    // Static initializer method
    public static Leaderboard[] init() {
        ArrayList<Leaderboard> leaders = new ArrayList<>();
        leaders.add(new Leaderboard("Tanav", 1, 1500, "2024-10-31"));
        leaders.add(new Leaderboard("Bob", 2, 1200, "2024-10-31"));
        leaders.add(new Leaderboard("Charlie", 3, 1000, "2024-10-31"));
        return leaders.toArray(new Leaderboard[0]);  // Converts List to array
    }

    // Main method
    public static void main(String[] args) {
        // Initialize leaderboard entries
        Leaderboard[] leaders = init();

        // Enhanced for loop to print each leaderboard entry
        for (Leaderboard leader : leaders) {
            System.out.println(leader);  // Print leaderboard object
        }
    }
}