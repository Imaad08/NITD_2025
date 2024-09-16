package com.nighthawk.spring_portfolio.hacks.calculator;

import java.util.function.BiFunction;

/**
 * TermOrOperator class is a subclass of Token
 * TermOrOperator is used to define the parts of a mathematical expression
 * - Values.  a string representation of the numerical value in the expression
 * - Operators.  define the operator token, precedence, and mathematical calculation
 * - Parenthesis.  used to group terms
 */
public class TermOrOperator extends Token {
    private final String value;

    // Constructor for values
    public TermOrOperator(String value) {
        this.value = value;
    }

    // Constructor for parenthesis
    public TermOrOperator(Character token) {
        super(token);
        this.value = null;
    } 

    // Constructor for operators
    public TermOrOperator(Character token, int precedence, BiFunction<Double, Double, Double> calculation) {
        super(token, precedence, calculation, 2);
        this.value = null;
    }

    // Constructor for operators
    public TermOrOperator(Character token, int precedence, BiFunction<Double, Double, Double> calculation, int numArgs) {
        super(token, precedence, calculation, numArgs);
        this.value = null;
    }

    // Get method for retrieving value
    public String getValue() {
        return value;
    }

    // toString method to return value according to type: value, operator, or parenthesis
    public String toString() {
        if (this.value == null) {
            return super.toString();
        }
        return this.value;
    }   
}