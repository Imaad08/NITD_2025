package com.nighthawk.spring_portfolio.hacks.methodsDataTypes;

/*
 * String is a reference type in Java, but it's not a reference type for char. 
 * It's a class that internally uses a char array to store its value, but from a user's perspective, it's just a String object.
 * String objects are indeed immutable in Java. Once a String object is created, it cannot be changed. 
 * If you perform an operation that seems to modify a String, what actually happens is a new String object is created. 
 * This is why a String will have a different location in memory after it's "changed".
 * When a String is passed as a parameter to a method, 
 * what's actually passed is the value of the reference to the String object (the memory address of the String), 
 * not the String object itself. This is why it's called pass-by-value. However, because the value that's being passed is a reference, 
 * you can use that reference to modify the object it points to, if the object is mutable. But since String objects are immutable, 
 * you can't modify them, so in a sense, String parameters behave like primitive types when passed to methods. 
 * Any "changes" to the String will not affect the original String object.
 */
public class StringTest {
    public String s = "abc"; // String data type on the heap
    
    public static void changeString(String sValue, StringTest sObject) {
        System.out.println("In changeString method");
        System.out.println("\t changeString method before: sValue = " + sValue + ", hash = " + System.identityHashCode(sValue));
        System.out.println("\t changeString method before: sObject.s = " + sObject.s + ", hash = " + System.identityHashCode(sValue));
        // Strings are immutable, if you perform an operation to modify a String, a new String object is created.
        // sValue[0] = 'A';  // not allowed 
        sValue = 'A' + sValue.substring(1);
        // sObject.s[0] = 'A'; // not allowed
        sObject.s = 'A' + sObject.s.substring(1);
        System.out.println("\t changeString method after: sValue = " + sValue + ", hash = " + System.identityHashCode(sValue)); // new string object
        System.out.println("\t changeString method after: sObject.s = " + sObject.s + ", hash = " + System.identityHashCode(sObject.s)); // new string object, not interned in string pool
    }

    public static void main(String[] args) {
        /*
         * Equivalent to:
         * String s = new String("abc");
         */
        String s = "abc"; // String data type on the heap, but char array is immutable (java reason: security and performance)
        // StringTest object is a new object that is created on the heap
        StringTest sObject = new StringTest();
        
        System.out.println("main method before: s = " + s + ", hash = " + System.identityHashCode(s)); //stack literal from string pool
        System.out.println("main method before: sObject.s = " + sObject.s + ", hash = " + System.identityHashCode(sObject.s)); //staic literal from string pool
        System.out.println("main method before: sObject = " + sObject + ", hash = " + System.identityHashCode(sObject));
    
        changeString(s, sObject); // s is a heap value but char array is immutable, sObject is a heap reference to StringTest object
        
        System.out.println("main method after: s = " + s + ", hash = " + System.identityHashCode(s));
        System.out.println("main method after: sObject.s = " + sObject.s + ", hash = " + System.identityHashCode(sObject.s));
        System.out.println("main method after: sObject = " + sObject + ", hash = " + System.identityHashCode(sObject)); // shows that changing sObject.s does not change the reference to the object
    }
}