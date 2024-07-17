package datastructures.worklists;

import cse332.interfaces.worklists.LIFOWorkList;

import java.util.NoSuchElementException;

/**
 * See cse332/interfaces/worklists/LIFOWorkList.java
 * for method specifications.
 */
public class ArrayStack<E> extends LIFOWorkList<E> {
    private E[] arr;
    private int size;

    public ArrayStack() {
        arr = (E[]) new Object[10];
        size = 0;
    }

    /**
     * Sets arr to a copy of arr with double the length of the number of elements.
     */
    private void resizeArray() {
        E[] newArr = (E[]) new Object[size * 2];
        for (int i = 0; i < size; i++) {
            newArr[i] = arr[i];
        }
        arr = newArr;
    }

    @Override
    public void add(E work) {
        if (size >= arr.length) {
            resizeArray();
        }
        arr[size] = work;
        size++;
    }

    @Override
    public E peek() {
        if (!hasWork()) {
            throw new NoSuchElementException();
        }
        return arr[size - 1];
    }

    @Override
    public E next() {
        if (!hasWork()) {
            throw new NoSuchElementException();
        }
        size--;
        return arr[size];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        arr = (E[]) new Object[10];
        size = 0;
    }
}
