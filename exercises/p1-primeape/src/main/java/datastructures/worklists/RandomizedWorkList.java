package datastructures.worklists;

import cse332.interfaces.worklists.WorkList;

public class RandomizedWorkList<E> extends WorkList<E> {

    private E[] arr;
    private int size;
    private int total;
    private boolean blocked;
    private boolean shuffled;
    public RandomizedWorkList(int capacity) {
        super();
        this.arr = (E[]) new Object[capacity];
        this.size = 0;
        this.total = 0;
        this.blocked = false;
        this.shuffled = false;
    }

    @Override
    public void add(E work) {
        if (blocked) {
            throw new IllegalStateException();
        } else if (size == arr.length) {
            int idx = (int) (Math.random() * total);
            if (idx < arr.length) {
                arr[idx] = work;
            }
            total++;
        } else {
            arr[size] = work;
            size++;
            total++;
        }
    }

    private void shuffle() {
        for (int i = 0; i < size; i++) {
            int idx = (int) (Math.random() * size);
            E tmp = arr[i];
            arr[i] = arr[idx];
            arr[idx] = tmp;
        }
        shuffled = true;
    }

    @Override
    public E peek() {
        if (!shuffled) {
            shuffle();
        }
        return arr[size];
    }

    @Override
    public E next() {
        if (!shuffled) {
            shuffle();
        }
        blocked = true;
        E item = arr[size - 1];
        size--;
        return item;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        this.arr = (E[]) new Object[arr.length];
        this.size = 0;
        this.total = 0;
        this.blocked = false;
        this.shuffled = false;
    }
}
