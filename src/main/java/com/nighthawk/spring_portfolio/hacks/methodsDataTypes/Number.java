package com.nighthawk.spring_portfolio.hacks.methodsDataTypes;

import java.util.ArrayList;

// Write a Class Number
public class Number {
    private String title = "Number";
    private String counter = "Index";
    private static int COUNT = 0;
    private final int number;
    private final int index;

    // Number has a zero Argument constructor
    // It initializes a random number between 3 and 36, ie the number of squirrels in class
    public Number() {
        int SIZE = 36; int MIN = 3; int RANGE = SIZE - MIN + 1;  // constants for initialization
        this.number = (int)(Math.random()*RANGE) + MIN;  // observe RANGE calculation and MIN offset
        this.index = Number.COUNT++;    // observe use of Class variable COUNT and post increment
    }

    // It contains a getter for the Random Number
    public int getNumber() {
        return this.number;
    }

    // It contains a getter for Index, or the order it was initialized
    public int getIndex() {
        return this.index;
    }

    // Make a setter to title the Random Number
    public void setTitle(String title) { this.title = title; }

    // Make a setter to name the Counter
    public void setCounter(String counter) { this.counter = counter; }

    // toString method for formatted output, this was not in requirement but is critical knowledge
    public String toString() {
        return this.title + ": " + this.getNumber() + " " + this.counter + ": " + this.getIndex();
    }

    // Write a tester method
    public static void main(String[] args) {

        // Create an ArrayList of Type Number, the ArrayList is called squirrels
        ArrayList<Number> squirrels = new ArrayList<>();

        // Initialize 10 squirrels of class type Number
        // Insert Number Object into ArrayList Squirrels in least to the greatest order using getNumber()
        final int SQUIRRELS = 10;
        for (int i = 0; i < SQUIRRELS; i++) {
            Number num = new Number();
            num.setTitle("Squirrels");
            num.setCounter("Days");
            // Insert in ordered position, this avoids sort algorithm
            for (int j = 0; true; j++) {
                // Conditions to insert
                if ((squirrels.size() == j) // empty or at end of list
                        || (num.getNumber() < squirrels.get(j).getNumber()) )  // less than current
                {
                    /* The java.util.ArrayList.add(int index, Object)
                       method inserts the specified object at the specified position in this list.
                       This is overload of the common java.util.ArrayList.add(Object)
                    */
                    squirrels.add(j, num); // insert in "j" position
                    break;  // break forever loop when inserted
                }
            }
        }

        // Added total/average, not in requirements
        int total = 0;
        for (Number squirrel : squirrels) {
            // Print a formatted message with number of Squirrels and Index
            System.out.println(squirrel);  // prints Object using toString() method
            total += squirrel.getNumber(); // running total, not in requirements
        }
        // Integer division requires cast for precision
        System.out.println("Total random squirrels: " + total + ", Number of days: " + squirrels.size());
        System.out.println("Average squirrels per day: " + total / (double) squirrels.size());
    }

}

