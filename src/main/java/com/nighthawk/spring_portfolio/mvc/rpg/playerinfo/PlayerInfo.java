package com.nighthawk.spring_portfolio.mvc.rpg.playerinfo;

import java.util.ArrayList;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity


public class PlayerInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private int age;
    private int period;

    public PlayerInfo(String name, int age, int period) {
        this.name = name;
        this.age = age;
        this.period = period;
    }
}

public static PlayerInfo[] init() {
        ArrayList<PlayerInfo> players = new ArrayList<>();
        players.add(new PlayerInfo("Tanav", 1, 1500));
        players.add(new PlayerInfo("Bob", 2, 1200));
        players.add(new PlayerInfo("Charlie", 3, 1000));
        return players.toArray(new PlayerInfo[0]);  // Converts List to array
    }

    // Main 
    public static void main(String[] args) {
        // Initialize leaderboard entries
        PlayerInfo[] players = init();

        // Enhanced for loop to print each leaderboard entry
        for (PlayerInfo leader : players) {
            System.out.println(players);  // Print leaderboard object
        }
    }

