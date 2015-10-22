package com.romelus_borucki.common.structures;

/**
 * A stack implementation, which folllows the LIFO paradigm.
 * Note: There is no automatic resizing of the stack after initialization, so declaring the capacity is essential.
 *
 * @author romelus
 */
public class Stack<T> {
    /**
     * Stack default size.
     */
    private static final int DEFAULT_SIZE = 10;
    /**
     * The backing array.
     */
    private T[] internalArray;
    /**
     * The capacity of the stack.
     */
    private int capacity;
    /**
     * Convenient index for number of items within the stack.
     */
    private int numElements = 0;

    /**
     * Default constructor.
     */
    public Stack() {
        this(DEFAULT_SIZE);
    }

    /**
     * Constructor for a fixed size stack.
     *
     * @param capacity the size of the stack
     */
    public Stack(final int capacity) {
        this.capacity = capacity;
        internalArray = (T[]) new Object[capacity];
    }

    /**
     * Adds an element to the stack.
     *
     * @param elem the element to add
     */
    public void push(final T elem) {
        if (numElements < capacity) {
            internalArray[(++numElements) - 1] = elem;
        }
    }

    /**
     * Removes the top element from the stack.
     *
     * @return the item that was removed
     */
    public T pop() {
        T retVal = null;
        if (numElements > 0) {
            retVal = internalArray[(numElements--) - 1];
        }
        return retVal;
    }

    /**
     * Getter for the number of elements contained within the stack.
     *
     * @return the number of elements
     */
    public int size() {
        return numElements;
    }

    /**
     * Getter for the stack's capacity.
     *
     * @return the capacity
     */
    public int getCapacity() {
        return capacity;
    }
}