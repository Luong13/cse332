package datastructures.worklists;

import cse332.interfaces.worklists.FixedSizeFIFOWorkList;

import java.util.NoSuchElementException;

/**
 * See cse332/interfaces/worklists/FixedSizeFIFOWorkList.java
 * for method specifications.
 */
public class CircularArrayFIFOQueue<E> extends FixedSizeFIFOWorkList<E> {
    private E[] arr;
    private int start;
    private int end;
    private int size;

    public CircularArrayFIFOQueue(int capacity) {
        super(capacity);
        arr = (E[]) new Object[capacity];
        start = 0;
        end = 0;
        size = 0;
    }

    @Override
    public void add(E work) {
        if (isFull()) {
            throw new IllegalStateException();
        }
        arr[end] = work;
        end = (end + 1) % arr.length;
        size++;
    }

    @Override
    public E peek(int i) {
        if (!hasWork()) {
            throw new NoSuchElementException();
        }
        if (i < 0 || i >= size()) {
            throw new IndexOutOfBoundsException();
        }
        return arr[(i + start) % arr.length];
    }

    @Override
    public E peek() {
        if (!hasWork()) {
            throw new NoSuchElementException();
        }
        return arr[start];
    }

    @Override
    public E next() {
        if (!hasWork()) {
            throw new NoSuchElementException();
        }
        E data = arr[start];
        start = (start + 1) % arr.length;
        size--;
        return data;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        arr = (E[]) new Object[arr.length];
        start = 0;
        end = 0;
        size = 0;
    }

    @Override
    public void update(int i, E value) {
        if (!hasWork()) {
            throw new NoSuchElementException();
        }
        if (i < 0 || i >= size()) {
            throw new IndexOutOfBoundsException();
        }
        arr[(i + start) % arr.length] = value;
    }

    @Override
    public int compareTo(FixedSizeFIFOWorkList<E> o) {
        return 0;
    }
}
