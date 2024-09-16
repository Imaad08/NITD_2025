package com.nighthawk.spring_portfolio.hacks.classDataStruct;

import java.util.Iterator;

/**
 * Queue: custom implementation
 * @author     John Mortensen
 *
 * 1. Uses custom LinkedList of Generic type T
 * 2. Implements Iterable
 * 3. "has a" LinkedList for head and tail
 */
public class Queue<T> implements Iterable<T> {
    LinkedList<T> head = null, tail = null;

    /**
     *  Add a new object at the end of the Queue,
     *
     * @param  data,  is the data to be inserted in the Queue.
     */
    public void add(T data) {
        // add new object to end of Queue
        LinkedList<T> tail = new LinkedList<>(data, null);

        if (this.head == null)  // initial condition
            this.head = this.tail = tail;
        else {  // nodes in queue
            this.tail.setNextNode(tail); // current tail points to new tail
            this.tail = tail;  // update tail
        }
    }

    /**
     *  Returns the data of head.
     *
     * @return  data, the dequeued data
     */
    public T delete() {
        T data = this.peek();
        if (this.tail != null) { // initial condition
            this.head = this.head.getNext(); // current tail points to new tail
            if (this.head != null) {
                this.head.setPrevNode(tail);
            }
        }
        return data;
    }

    /**
     *  Returns the data of head.
     *
     * @return  this.head.getData(), the head data in Queue.
     */
    public T peek() {
        return this.head.getData();
    }

    /**
     *  Returns the head object.
     *
     * @return  this.head, the head object in Queue.
     */
    public LinkedList<T> getHead() {
        return this.head;
    }

    /**
     *  Returns the tail object.
     *
     * @return  this.tail, the last object in Queue
     */
    public LinkedList<T> getTail() {
        return this.tail;
    }

    /**
     *  Returns the iterator object.
     *
     * @return  this, instance of object
     */
    public Iterator<T> iterator() {
        return new QueueIterator<>(this);
    }
}

/**
 * Queue Iterator
 *
 * 1. "has a" current reference in Queue
 * 2. supports iterable required methods for next that returns a data object
 */
class QueueIterator<T> implements Iterator<T> {
    LinkedList<T> current;  // current element in iteration

    // Returns the head of the list
    public QueueIterator(Queue<T> q) {
        current = q.getHead();
    }

    // hasNext informs if next element exists
    public boolean hasNext() {
        return current != null;
    }

    // next returns data object and advances to next position in queue
    public T next() {
        T data = current.getData();
        current = current.getNext();
        return data;
    }
}

/**
 * Queue Manager
 * 1. "has a" Queue
 * 2. support management of Queue tasks (aka: titling, adding a list, printing)
 */
class QueueManager<T> {
    // queue data
    static public boolean DEBUG = false;
    private final String name; // name of queue
    private long count = 0; // number of objects in queue
    private final Queue<T> queue = new Queue<>(); // queue object

    /**
     *  Queue constructor
     *  Title with empty queue
     */
    public QueueManager(String name) {
        this.name = name;
    }

    /**
     *  Queue constructor
     *  Title with series of Arrays of Objects
     */
    @SafeVarargs
    public QueueManager(String name, T[]... seriesOfObjects) {
        this.name = name;
        this.addList(seriesOfObjects);
    }

    /**
     *  Queue constructor
     *  Title with empty queue
     */
    public long size() {
        return count;
    }

    /**
     * Add a list of objects to queue
     */
    @SafeVarargs
    public final void addList(T[]... seriesOfObjects) {
        for (T[] objects: seriesOfObjects)
            for (T data : objects) {
                this.add(data);
            }
    }

    /**
     * Delete or dequeue all objects
     */
    public void deleteList() {
        while (this.queue.getHead() != null) {
            this.delete();
        }
    }

    /**
     * Add an object to queue
     */
    public void add(T data) {
        this.queue.add(data);
        this.count++;
        if (DEBUG) {
            System.out.println("Enqueued data: " + data);
            printQueue();
        }
    }

    /**
     * Remove an object from the queue
     */
    public T delete() {
        T data = this.queue.delete();
        this.count--;
        if (DEBUG) {
            System.out.println("Dequeued data: " + data);
            printQueue();
        }
        return data;
    }

    /**
     * Print objects from queue
     */
    public void printQueue() {
        System.out.print(this.name + " count: " + this.size());
        System.out.print(", data: ");
        if (this.queue.getHead() == null) {
            System.out.println( "null");
        } else {
            for (Object o : this.queue)
                System.out.print(o + " ");
            System.out.println();
        }
    }
}

/**
 * Driver Class
 * Tests queue with string, integers, and mixes of Classes and types
 */
class QueueTester {
    public static void main(String[] args)
    {
        // Create iterable Queue of Words
        QueueManager.DEBUG = false;
        String[] words = new String[] { "seven", "slimy", "snakes", "sallying", "slowly", "slithered", "southward"};
        QueueManager<String> qWords = new QueueManager<>("Words", words );
        qWords.printQueue();
        qWords.deleteList();

        // Create iterable Queue of Integers
        QueueManager.DEBUG = false;
        Object[] numbers = new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        QueueManager<Object> qNums = new QueueManager<>("Integers", numbers );
        qNums.printQueue();
        qNums.deleteList();

        // Create iterable Queue of NCS Generics
        QueueManager.DEBUG = false;
        Animal.setOrder(Animal.KeyType.name);
        Alphabet.setOrder(Alphabet.KeyType.letter);
        Cupcakes.setOrder(Cupcakes.KeyType.flavor);
        // Illustrates use of a series of repeating arguments
        QueueManager<Generics> qGenerics = new QueueManager<>("Custom Generics",
                Alphabet.alphabetData(),
                Animal.animalData(),
                Cupcakes.cupCakeData()
        );
        qGenerics.printQueue();
        qGenerics.deleteList();

        // Create iterable Queue of Mixed types of data
        QueueManager.DEBUG = false;
        QueueManager<Object> qMix = new QueueManager<>("Mixed");
        qMix.add("Start");
        qMix.addList(
                words,
                numbers,
                Alphabet.alphabetData(),
                Animal.animalData(),
                Cupcakes.cupCakeData()
        );
        qMix.add("End");
        qMix.printQueue();
        qMix.deleteList();
    }
}
