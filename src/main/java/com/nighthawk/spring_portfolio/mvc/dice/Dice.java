package com.nighthawk.spring_portfolio.mvc.dice;

import java.util.Random;

// @Data
// @NoArgsConstructor
// // @AllArgsConstructor
// @Entity
public class Dice {
    private double winChance;
    private double betSize;

    public Dice(double winChance, double betSize) {
        this.winChance = winChance;
        this.betSize = betSize;
    }
    public double calculateWin(){
        Random rand = new Random();
        double rand_double = rand.nextDouble(1);
        if (rand_double <= winChance) {
            return this.betSize*(1/this.winChance - 1) * this.winChance * (1- 0.05/(1-this.winChance));
        } else { 
            return 0-this.betSize;
        }
    }
    public static void main(String[] args) {
        Dice newBet = new Dice(0.6, 100.00);
        double total = 0;
        for (int i = 0; i < 100; i++){
            total += newBet.calculateWin();
            System.out.println(newBet.calculateWin());
        }
        System.out.println("expected value is " + total/100);
    }

}
