package com.nighthawk.spring_portfolio.hacks.streamChain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FruitBasket {
    private List<Fruit> fruits = new ArrayList<>();

    /**
     * Adds a fruit to the fruits list.
     *
     * <p>This method enables method chaining, allowing you to repeatedly call .addFruit() 
     * This is the builder pattern common to streams.</p>
     *
     * @param fruit the fruit to be added to the fruits list
     * @return the current instance of FruitBasket
     */
    public FruitBasket addFruit(Fruit fruit) {
        this.fruits.add(fruit);
        return this;
    }

    /**
     * Filters the fruits in the basket by climate.
     *
     * <p>This method uses the stream() method to create a stream from the list of fruits, then uses the filter() method
     * to filter out only the fruits that match the given climate.</p>
     *
     * @param climate the climate to filter by
     * @return a list of fruits that grow in the given climate
     */
    public List<Fruit> filterByClimate(Climate climate) {
        return fruits.stream()
            .filter(fruit -> fruit.getClimate() == climate)
            .collect(Collectors.toList());
    }

    /**
     * Filters the fruits in the basket by state.
     *
     * <p>This method uses the stream() method to create a stream from the list of fruits, then uses the filter() method
     * to filter out only the fruits that can be grown in the given state.</p>
     *
     * @param state the state to filter by
     * @return a list of fruits that can be grown in the given state
     */
    public List<Fruit> filterByState(State state) {
        return fruits.stream()
            .filter(fruit -> state.getClimates().contains(fruit.getClimate()))
            .collect(Collectors.toList());
    }

    /**
     * Returns a list of states where a given fruit can be grown.
     *
     * <p>This method uses the stream() method to create a stream from the array of all states, then uses the filter() method
     * to filter out only the states where the given fruit can be grown.</p>
     *
     * @param fruitName the name of the fruit to check
     * @return a list of states where the given fruit can be grown
     */
    public List<State> whereCanIGrow(String fruitName) {
        return Arrays.stream(State.values())
            .filter(state -> fruits.stream()
                .anyMatch(fruit -> fruit.getName().equals(fruitName) && state.getClimates().contains(fruit.getClimate())))
            .collect(Collectors.toList());
    }

    /**
     * Returns a list of climates in which a given fruit can be grown.
     *
     * <p>This method uses the stream() method to create a stream from the array of all climates, then uses the filter() method
     * to filter out only the climates where the given fruit can be grown.</p>
     *
     * @param fruitName the name of the fruit to check
     * @return a list of climates where the given fruit can be grown
     */
    public List<Climate> whatClimateCanIGrow(String fruitName) {
        return Arrays.stream(Climate.values())
            .filter(climate -> fruits.stream()
                .anyMatch(fruit -> fruit.getName().equals(fruitName) && fruit.getClimate() == climate))
            .collect(Collectors.toList());
    }
    /**
     * Returns a string representation of the fruits in the basket.
     *
     * @return a string representation of the fruits in the basket
     */
    public String toString() {
        return fruits.toString();
    }

    public static void main(String[] args) {
        FruitBasket basket = new FruitBasket();
        // Add fruits to the collection using chaining
        basket
            .addFruit(new Fruit("Apple", Climate.TEMPERATE))
            .addFruit(new Fruit("Banana", Climate.TROPICAL))
            .addFruit(new Fruit("Cherry", Climate.TEMPERATE))
            .addFruit(new Fruit("Orange", Climate.SUBTROPICAL))
            .addFruit(new Fruit("Peach", Climate.SUBTROPICAL))
            .addFruit(new Fruit("Pear", Climate.TEMPERATE))
            .addFruit(new Fruit("Pineapple", Climate.TROPICAL))
            ;

        // Print Tropical fruits using a foreach loop
        System.out.println(Climate.TROPICAL + "  fruits are:");
        for (Fruit fruit : basket.filterByClimate(Climate.TROPICAL)) {
            System.out.println(fruit);
        }

        // Print fruits that can be grown in California using a foreach method and lambda expression
        System.out.println(State.CALIFORNIA + " fruits are: ");
        List<Fruit> subtropicalFruits = basket.filterByState(State.CALIFORNIA);
        subtropicalFruits.forEach(fruit -> System.out.println(fruit));
       
        // Print where Oranges can be grown using a method reference
        System.out.println("Where can I grow an Orange?");
        basket.whereCanIGrow("Orange").forEach(System.out::println);

        // Print the climate in which Apples can be grown using a method reference
        System.out.println("What climate can I grow an Apple?");
        basket.whatClimateCanIGrow("Apple").forEach(System.out::println);
    }
}
