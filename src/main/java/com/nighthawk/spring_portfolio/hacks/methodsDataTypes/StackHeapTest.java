package com.nighthawk.spring_portfolio.hacks.methodsDataTypes;

public class StackHeapTest {
    public int n = 5; // Primitive data type on the heap
    
    public static void changeInt(int nValue, int nRefN, StackHeapTest nRef) {
        System.out.println("In changeInt method");
        System.out.println("\tBefore nValue += 10: nValue = " + nValue);
        System.out.println("\tBefore nRefN += 10: nRefN = " + nRefN);
        System.out.println("\tBefore nRef.n += 10: nRef.n = " + nRef.n);
        nValue += 10;
        nRefN += 10;
        nRef.n += 10;
        System.out.println("\tAfter nValue += 10: nValue = " + nValue);
        System.out.println("\tAfter nRefN += 10: nRefN = " + nRefN);
        System.out.println("\tAfter nRef.n += 10: nRef.n = " + nRef.n);
    }

    public static void main(String[] args) {
        int n = 5; // Primitive data type on the stack
        StackHeapTest nRef = new StackHeapTest();
        System.out.println("Main method before changeInt(nValue): nValue = " + n + ", hash = " + System.identityHashCode(n));  // auto-boxing for HashCode
        System.out.println("Main method before changeInt(nRef.n): nRef.n = " + nRef.n + ", hash = " + System.identityHashCode(nRef.n));
        System.out.println("Main method before changeInt(nRef): nRef = " + nRef + ", hash = " + System.identityHashCode(nRef));
    
        changeInt(n, nRef.n, nRef); // stack by value, heap by value, heap by reference
    
        System.out.println("Main method after changeInt(nValue): nValue = " + n + ", hash = " + System.identityHashCode(n)); 
        System.out.println("Main method after changeInt(nRef.n): nRef.n = " + nRef.n + ", hash = " + System.identityHashCode(nRef.n));
        System.out.println("Main method after changeInt(nRef): nRef = " + nRef + ", hash = " + System.identityHashCode(nRef));

    }
}