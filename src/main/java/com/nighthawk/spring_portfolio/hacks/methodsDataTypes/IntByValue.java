package com.nighthawk.spring_portfolio.hacks.methodsDataTypes;

public class IntByValue {
    
    public static void changeInt(int n) {
        System.out.println("In changeInt method");
        System.out.println("\tBefore n += 10: n = " + n); // prints 5
        n = n += 10;
        System.out.println("\tAfter n += 10: n = " + n); // prints 10
    }

    public static void main(String[] args) {
        int n = 5;
        System.out.println("Main method before changeInt(n): n = " + n); // prints 5
        changeInt(n);
        System.out.println("Main method after changeInt(n): n = " + n); // still prints 5
    }
}