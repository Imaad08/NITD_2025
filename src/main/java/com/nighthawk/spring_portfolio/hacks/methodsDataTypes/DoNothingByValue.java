package com.nighthawk.spring_portfolio.hacks.methodsDataTypes;

public class DoNothingByValue {
    public int[] arr;
    public int val;
    public String word;


    // changed to show what is happening
    public DoNothingByValue (int [] arr, int val, String word) {
        this.arr = new int[5];
        this.val = 0;
        this.word = word.substring(0, 5);

        System.out.print("constructor: ");
        for (int k = 0; k < arr.length; k++) {
            arr[k] = 0;                          // int array is initialized to 0's, not needed
            System.out.print(arr[k] + " ");

        }
        System.out.println(this.word);

    }

    // Local instance variables
    // IntelliJ shows that something is wrong, calling the values passed as parameters as local
    public static void changeIt(int [] arr, int val, String word) {
        arr = new int[5];
        val = 0;
        word = word.substring(0, 5);

        System.out.print("changeIt: "); // added
        for (int k = 0; k < arr.length; k++) {
            arr[k] = 0;
            System.out.print(arr[k] + " "); // added
        }
        System.out.println(word);   // added

    }

    // Variable name are Reference
    // names of variables make no difference, they are just references to addresses
    public static void changeIt2(int [] nums, int value, String name) {
        nums = new int[5];           // new creates new memory address
        value = 0;                   // primitives are pass by value
        name = name.substring(0, 5); // all wrapper classes have automatic "new", same as word = new String(word.substring(0, 5));

        // this loop changes nums locally
        System.out.print("changeIt2: ");
        for (int k = 0; k < nums.length; k++) {
            nums[k] = 0;
            System.out.print(nums[k] + " ");
        }
        System.out.println(name);

    }


    // If you want to change values, think about Return values, but you are limited to one in Java
    // changed to show what is happening
    public static String changeIt3(int [] arr, String word) {
        word = new String(word.substring(0, 5));    // wrapper class does a "new" on any assignment

        System.out.print("changeIt3: ");
        for (int k = 0; k < arr.length; k++) {
            arr[k] = 0;                          // int array is initialized to 0's, not needed
            System.out.print(arr[k] + " ");

        }
        System.out.println(word);

        return word;
    }

    // Variable inside of Object Triple are references
    public static Triple<int[], Integer, String> changeIt4(Triple<int[], Integer, String> T) {
        T.setOne(new int[5]);
        T.setTwo(0);                   // primitives are pass by value
        T.setThree(T.getThree().substring(0, 5)); // all wrapper classes have automatic "new", same as word = new String(word.substring(0, 5));

        // this loop changes nums locally
        System.out.print("changeIt4: ");
        for (int i : T.getOne()) {
            System.out.print(i + " ");
        }
        System.out.println(T.getThree());

        return T;
    }


    // Original method changed to main in order to be a Tester
    public static void main(String[] args) {
        // Does nothing
        int [] nums = {1, 2, 3, 4, 5};
        int value = 6;
        String name = "blackboard";
        System.out.println("Do Nothings");
        // dumb and useless
        changeIt(nums, value, name);
        // dumber and useless
        changeIt2(nums, value, name);
        System.out.print("main: ");
        for (int k = 0; k < nums.length; k++) {
            System.out.print (nums[k] + " ");
        }
        System.out.print(value + " ");
        System.out.print(name);

        System.out.println();
        System.out.println();


        // int[] by reference, return value  -- not complete
        System.out.println("Limited return");
        int[] nums2 = {1, 2, 3, 4, 5};
        value = 6;
        name = "limited";
        name = changeIt3(nums2, name);
        System.out.print("main2: ");
        for (int num : nums2) {
            System.out.print(num + " ");
        }
        System.out.print(value + " ");
        System.out.print(name);

        System.out.println();
        System.out.println();


        // Class/Object
        System.out.println("Do Something with Class");
        int[] nums3 = {1, 2, 3, 4, 5};
        value = 6;
        name = "classy";
        DoNothingByValue doSomething = new DoNothingByValue(nums3, value, name);

        System.out.print("main3: ");
        for (int num : doSomething.arr) {      // should be teaching enhanced for loop on arrays
            System.out.print(num + " ");
        }
        System.out.print(doSomething.val + " ");
        System.out.print(doSomething.word);

        System.out.println();
        System.out.println();

        // Generics
        System.out.println("Do Something with Generics");
        int[] nums4 = {1, 2, 3, 4, 5};
        value = 6;
        name = "generics";
        Triple<int[],Integer,String> tri = changeIt4(new Triple<>(nums4, value, name));

        System.out.print("main: ");
        for (int num : tri.getOne())
            System.out.print (num + " ");

        System.out.print(tri.getTwo() + " ");
        System.out.print(tri.getThree());
    }

}

// This class can be used to pass variables by reference
class Triple<T1, T2, T3> {
    T1 one;
    T2 two;
    T3 three;

    Triple(T1 one, T2 two, T3 three) {
        this.one = one;
        this.two = two;
        this.three = three;
    }

    public T1 getOne() { return this.one; }
    public void setOne(T1 one) { this.one = one; }

    public T2 getTwo() { return this.two; }
    public void setTwo(T2 two) { this.two = two; }

    public T3 getThree() { return this.three; }
    public void setThree(T3 three) { this.three = three; }
}