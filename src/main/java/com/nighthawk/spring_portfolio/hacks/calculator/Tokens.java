package com.nighthawk.spring_portfolio.hacks.calculator;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.HashMap;

/**
 * Terms class is a collection of Term objects
 * Terms are used to define the parts of a mathematical expression
 * - Operators.  define the operator, precedence, and mathematical calculation
 * - Parenthesis.  used to seperate and group terms
 * - Space. Used to seperate terms
 * 
 * A Map is choosen as the data structure because it enables fast lookups of Terms
 */
public class Tokens {
    // Terms are stored in map, using Term token as the key
    private Map<Character, TermOrOperator> map;

    // Constructor initializes map
    public Tokens() {
        this.map = new HashMap<>();
    }

    // Put method for adding Parenthesis and Space
    public void put(Character token) {
        this.map.put(token, new TermOrOperator(token));
    }

    // Put method for adding Operators, precedence, and calculation
    public void put(Character token, int precedence, BiFunction<Double, Double, Double> calculation, int numArgs) {
        this.map.put(token, new TermOrOperator(token, precedence, calculation, numArgs));
    }

    // Put method for adding Operators, precedence, and calculation
    public void put(Character token, int precedence, BiFunction<Double, Double, Double> calculation) {
        this.map.put(token, new TermOrOperator(token, precedence, calculation));
    }

    // Get method for retrieving Term based on token lookup  
    public TermOrOperator get(Character token) {
        return this.map.get(token);
    }

    // Get precedence method for retrieving precedence based on token lookup
    public int getPrecedence(Character token) {
        return this.map.get(token).getPrecedence();
    }

    // Contains method for checking if token exists in map
    public boolean contains(Character token) {
        return this.map.containsKey(token);
    }

    // toString method for converting entire map to string
    public String toString() {
        return this.map.toString();
    }

}
