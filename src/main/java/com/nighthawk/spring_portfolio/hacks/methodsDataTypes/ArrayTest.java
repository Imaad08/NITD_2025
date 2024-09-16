package com.nighthawk.spring_portfolio.hacks.methodsDataTypes;

import java.util.Arrays;

public class ArrayTest {
    String[] arr = {"abc","def","xyz"};

    public static void changeArray(String[] aLocalRef, ArrayTest aObject) {
        System.out.println("In changeArray method");
        System.out.println("\t changeArray method before: aLocalRef = " + Arrays.toString(aLocalRef) + ", hash = " + System.identityHashCode(aLocalRef));
        System.out.println("\t changeArray method before: aObject = " + Arrays.toString(aObject.arr) + ", hash = " + System.identityHashCode(aObject));
        aLocalRef[2] = "ghi";  // allowed as content in arrays is mutable, array is passed by reference 
        aObject.arr[2] = "ghi";
        System.out.println("\t changeArray method after: aLocalRef = " + Arrays.toString(aLocalRef) + ", hash = " + System.identityHashCode(aLocalRef));
        System.out.println("\t changeArray method after: aObject = " + Arrays.toString(aObject.arr) + ", hash = " + System.identityHashCode(aObject));
    }

    public static void main(String[] args) {
        /*
         * Equivalent to:
         * String[] arr = new String[3];
         * arr[0] = "abc";
         * arr[1] = "def";
         * arr[2] = "xyz";
         */
        String[] arr = {"abc","def","xyz"}; // Reference data type is on the heap, a new object is created on the heap
        // ArrayTest object is a new object that is created on the heap
        ArrayTest aObject = new ArrayTest();
        
        System.out.println("main method before:  arr= " + Arrays.toString(arr)  + ", hash = " + System.identityHashCode(arr));
        System.out.println("main method before: aObject = " + Arrays.toString(aObject.arr) + ", hash = " + System.identityHashCode(aObject));
    
        changeArray(arr, aObject); // arr is heap value, aObject is heap reference to ArrayTest object 
        
        System.out.println("main method before: arr = " + Arrays.toString(arr) + ", hash = " + System.identityHashCode(arr));
        System.out.println("main method before: aObject = " + Arrays.toString(aObject.arr) + ", hash = " + System.identityHashCode(aObject));
    }
}