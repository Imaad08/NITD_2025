package com.nighthawk.spring_portfolio.hacks.calculator;

import java.util.ArrayList;
import java.util.Stack;

/** In mathematics,
    an expression or mathematical expression is a finite combination of symbols that is well-formed
    according to rules that depend on the context.
   In computers,
    expression can be hard to calculate with precedence rules and user input errors
    to handle computer math we often convert strings into reverse polish notation
*/
public class Calculator {
    // Key instance variables
    private final String expression;
    private ArrayList<TermOrOperator> terms = new ArrayList<>();
    private ArrayList<TermOrOperator> rpnTerms = new ArrayList<>();
    private Tokens operators = new Tokens();
    private Tokens seperators = new Tokens();
    private Double result = 0.0;

    /**
     * Calculator constructor to create a calculation object for an expression
     * This calculates the expression and saves the result and intermediate steps in instance variables
     * 
     * @param expression
     */
    public Calculator(String expression) {   
        // set up tokens used in an calculator
        initOperators();
        initSeperators();

        // original expression
        this.expression = expression;

        // parse expression into individual terms
        this.termTokenizer();

        // place terms into reverse polish notation
        this.termsToRPN();

        // calculate reverse polish notation expression into a result
        this.rpnToResult();
    }

    /** 
     * Method to initialize operators, precedence, and calculation
     * Fundamental to data structures is the ability to store and retrieve data quickly
     * In this case, we are using a map to store operators and their precedence and calculation methods.
     * 
     * Note, that through overloaded methods, we can store operators as Terms with different numbers of arguments
     * This is specifically useful, in this case, for unary operators like square root
     */
    private void initOperators() {
        // Operators contain a token, precedence, and calculation using BiFunction
        operators.put('*', 3, (a, b) -> a * b);
        operators.put('/', 3, (a, b) -> a / b);
        operators.put('%', 3, (a, b) -> a % b);
        operators.put('+', 4, (a, b) -> a + b);
        operators.put('-', 4, (a, b) -> a - b);
        operators.put('^', 2, (a, b) -> Math.pow(a, b)); // Power operation
        operators.put('√', 1, (a, b) -> Math.sqrt(a), 1); // Square root operation
    }

    /**
     * Method to initialize seperators
     * Seperators are used to seperate terms 
     * Additionally, the parenthesis are used to group terms and operations
     *
     * Note, that through overloaded methods, we can store seperators as Terms with different numbers of arguments 
     */
    private void initSeperators() {
        // Seperators contain a token 
        seperators.put(' ');
        seperators.put('(');
        seperators.put(')');
    }

    /**
     * Term Tokenizer takes original expression and converts it to ArrayList of mathematical terms and values
     * Populates the this.terms instance of type ArrayList<TermOrOperator>
     * In essence, this method tokenizes the expression into individual terms/cells in the ArrayList 
     */ 
    private void termTokenizer() {
        int start = 0;  // term split starting index
        StringBuilder multiCharTerm = new StringBuilder();    // term holder
        for (int i = 0; i < this.expression.length(); i++) {
            Character c = this.expression.charAt(i);
            
            if ( operators.contains(c) || seperators.contains(c)  ) {
                // 1st check for working term and add if it exists
                if (multiCharTerm.length() > 0) {
                    this.terms.add(new TermOrOperator(this.expression.substring(start, i)));
                }
                // Add operator or parenthesis term to list
                TermOrOperator t = operators.get(c);
                if (t == null) {
                    t = seperators.get(c);
                }
                if (t != null && t.getToken() != ' ') {
                    this.terms.add(t);
                }

                // Get ready for next term
                start = i + 1;
                multiCharTerm = new StringBuilder();
            } else {
                // multi character terms: numbers, functions, perhaps non-supported elements
                // Add next character to working term
                multiCharTerm.append(c);
            }

        }
        // Add last term
        if (multiCharTerm.length() > 0) {
            this.terms.add(new TermOrOperator(this.expression.substring(start)));
        }
    }

    /**
     * This method populates the this.rpnTerms instance of type ArrayList<TermOrOperator> from the this.terms
     * Observe the inorder shift from before (terms) to after (termsToRPN) reorder
     * This reordering is called Reverse Polish Notation (RPN)
     * The terms are reordered by parenthesis and operator precedence, also called postfix notation
     * RPN is commonly used in computer science to evaluate mathematical expressions
     * RPN originated with with the Polish mathematician Jan Łukasiewicz
     * RPN was later popularized by the Hewlett-Packard company in the 1970s with their scientific calculators
     * 
     */ 
    private void termsToRPN () {
        // A stack is used to push and pop calculation for grouping and precedence
        Stack<TermOrOperator> operatorStack = new Stack<>();

        // Process each term
        for (TermOrOperator term : terms) {
            // term is a marker for grouping
            if (term.getToken() == '(') { // open parenthesis
                operatorStack.push(term);
            // term is a marker to empty group of terms to matching parenthesis
            } else if (term.getToken() == ')') { // close parenthesis
                while (operatorStack.peek() != null && operatorStack.peek().getToken() != '(') {
                    rpnTerms.add(operatorStack.pop());
                }
                operatorStack.pop(); // remove open parenthesis
            // term is an operator, shuffle terms between Operator stack and RPN array list to maintain precedence
            } else if (operators.contains(term.getToken())) {
                while (!operatorStack.isEmpty() && operators.contains(operatorStack.peek().getToken()) && term.isPrecedent(operatorStack.peek())) {
                    rpnTerms.add(operatorStack.pop());
                }
                operatorStack.push(term);
            // term is a value, add it directly to RPN array list
            } else {
                this.rpnTerms.add(term);
            }
        }
        // Empty the operator stack to RPN array list
        while (!operatorStack.isEmpty()) {
            rpnTerms.add(operatorStack.pop());
        }
    }

    /**
     * The method takes this.rpnTerms and calculates them into a final result
     * Values proceed the operators, value(s) are popped from the this.rpmTerms and pushed onto the calcStack
     * Operators are applied to the value(s) in the calcStack and the result is pushed back onto the calcStack 
     * The final result is the remaining value on the calcStack with the this.rpnTerms is empty 
     * The final result is stored in this.result 
     * 
     */ 
    private void rpnToResult()
    {
        // stack is used to hold operands and each calculation
        Stack<Double> calcStack = new Stack<Double>();

        // RPN is processed, ultimately calcStack has final result
        for (TermOrOperator term : this.rpnTerms)
        {
            Double operand1 = 0.0, operand2 = 0.0, result = 0.0;

            // If the token is an operator, calculate
            if (operators.contains(term.getToken()))
            {
                if (term.getNumArgs() == 1) {
                    operand1 = calcStack.pop();
                } else {
                    // Pop the two top entries
                    operand2 = calcStack.pop();
                    operand1 = calcStack.pop();
                }
                // Calculate result and push back onto the stack
                result = term.calculate(operand1, operand2);
                calcStack.push( result );
            }
            // else the token is a number push it onto the stack
            else
            {
                calcStack.push(Double.valueOf(term.getValue()));
            }
        }
        // Pop final result and set as final result for expression
        this.result = calcStack.pop();
    }

    // Print the expression, terms, and result from the instance variables
    public String toString() {
        return ("Original expression: " + this.expression + "\n" +
                "Tokenized expression: " + this.terms.toString() + "\n" +
                "Reverse Polish Notation: " +this.rpnTerms.toString() + "\n" +
                "Final result: " + String.format("%.2f", this.result));
    }

    // Tester method contains a few examples of mathematical expressions
    public static void main(String[] args) {
        Calculator simpleMath = new Calculator("100 + 200  * 3");
        System.out.println("Simple Math\n" + simpleMath);

        System.out.println();

        Calculator parenthesisMath = new Calculator("(100 + 200)  * 3");
        System.out.println("Parenthesis Math\n" + parenthesisMath);

        System.out.println();

        Calculator decimalMath = new Calculator("100.2 - 99.3");
        System.out.println("Decimal Math\n" + decimalMath);

        System.out.println();

        Calculator moduloMath = new Calculator("300 % 200");
        System.out.println("Modulo Math\n" + moduloMath);

        System.out.println();

        Calculator divisionMath = new Calculator("300/200");
        System.out.println("Division Math\n" + divisionMath);

        System.out.println();

        Calculator pythagoreanMath = new Calculator("√(3^2 + 4^2)");
        System.out.println("Pythagorean Theorem\n" + pythagoreanMath);

    }
}