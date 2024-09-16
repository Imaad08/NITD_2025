package com.nighthawk.spring_portfolio.hacks.methodsDataTypes;

public class IntegerByValueOrReference {
    
    public static void changeInteger(Integer n) {
        System.out.println("In changeInteger method");
        System.out.println("\tBefore change: n = " + 
                            n + // prints 5
                            " hash code = " + 
                            n.hashCode()); 

        n += 10;  // behind the scenes, this is:  n = new Integer(n+10)
        
        System.out.println("\tAfter change: n = " + 
                            n + // prints 15
                            " hash code = " + 
                            n.hashCode()); 
    }

    public static void main(String[] args) {
        Integer n = 5;
        System.out.println("Main method before changeInteger(n): n = " + 
                            n + // prints 5
                            " hash code = " + 
                            n.hashCode()); 

        changeInteger(n);
        
        System.out.println("Main method after changeInteger(n): n = " + 
                            n +  // now prints 15
                            " hash code = " + 
                            n.hashCode());     
    }
}
