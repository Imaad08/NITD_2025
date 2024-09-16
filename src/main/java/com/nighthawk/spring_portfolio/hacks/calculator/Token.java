package com.nighthawk.spring_portfolio.hacks.calculator;

import java.util.function.BiFunction;

/**
 * Token class
 * A Token is the key component of a mathematical expression
 * - Operators.  define the operator token, precedence, and mathematical calculation
 * - Parenthesis.  used to group terms
 */
public class Token {
    private final Character token;
    private final int precedence;
    private final BiFunction<Double, Double, Double> calculation;
    private final int numArgs;

    // Constructor for passive token, used for non-operator tokens
    public Token() {
        this('0');  // telescope to next constructor
    }

    // Constructor for simple token, used for parenthesis
    public Token(Character token) {
        this(token,0,null,0); // telescope to next constructor
    }

    // Constructor for operators, contains precedence and calculation method
    public Token(Character token, int precedence,  BiFunction<Double, Double, Double> calculation, int numArgs) {
        this.token = token;
        this.precedence = precedence;
        this.calculation = calculation;
        this.numArgs = numArgs;
    }

    // Getter for token
    public Character getToken() {
        return token;
    }

    // Getter for precedence
    public int getPrecedence() {
        return precedence;
    }

    public int getNumArgs() {
        return numArgs;
    }

    // Is precedent calculation
    public boolean isPrecedent(Token token) {
        return this.precedence > token.getPrecedence();
    }

    // Math calculation for operator and operands
    public Double calculate(Double operand1, Double operand2) {
        return this.calculation.apply(operand1, operand2);
    }

    // String return for token / operator
    public String toString() {
        return this.token.toString();
    }
    
}
