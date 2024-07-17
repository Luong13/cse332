package datastructures.worklists;

import cse332.interfaces.worklists.PriorityWorkList;

import java.util.NoSuchElementException;

/**
 * See cse332/interfaces/worklists/PriorityWorkList.java
 * for method specifications.
 */
public class MinFourHeapComparable<E extends Comparable<E>> extends PriorityWorkList<E> {
    /* Do not change the name of this field; the tests rely on it to work correctly. */
    private E[] data;
    private int size;

    public MinFourHeapComparable() {
        this.data = (E[]) new Comparable[10];
        this.size = 0;
    }

    @Override
    public boolean hasWork() {
        if (size > 0){
            return true;
        }
        return false;
    }

    @Override
    public void add(E work) {
        if (this.size >= this.data.length) {
            resizeArr();
        }
        data[size] = work;
        int workIndex = size;
        int parentIndex = (workIndex - 1) / 4;
        while (data[workIndex].compareTo(data[parentIndex]) < 0 && workIndex > 0) {
            E temp = data[parentIndex];
            data[parentIndex] = data[workIndex];
            data[workIndex] = temp;
            workIndex = parentIndex;
            parentIndex = (workIndex - 1) / 4;
        }
        this.size++;
    }

    @Override
    public E peek() {
        if (!hasWork()){
            throw new NoSuchElementException();
        }
        return data[0];
    }

    @Override
    public E next() {
        if (!hasWork()) {
            throw new NoSuchElementException();
        }
        E result = data[0];
        data[0] = data[size - 1];
        int workIndex = 0;
        int minChildIndex = 0;
        E minValue;
        boolean cont = true;
        while (cont) {
            //minValue means ?
            minValue = data[workIndex];
            cont = false;
            for (int i = 1; i <= 4; i++) {
                //second requirement checks if the current child is smaller than the smallest child until then
                //also need to check if current child is a leaf node
                if ((4 * workIndex + i) < this.size && data[4 * workIndex + i].compareTo(minValue) < 0 && data[4 * workIndex + i].compareTo(data[minChildIndex]) < 0) {
                    minChildIndex = 4 * workIndex + i;
                }
            }
            if (data[minChildIndex].compareTo(data[workIndex]) < 0) {
                cont = true;
                E temp = data[workIndex];
                data[workIndex] = data[minChildIndex];
                data[minChildIndex] = temp;
                workIndex = minChildIndex;
            }
        }
        size--;
        return result;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        this.data = (E[]) new Comparable[10];
        this.size = 0;
    }

    public void resizeArr() {
        E[] newArr = (E[]) new Comparable[size * 2];
        for (int i = 0; i < size; i++) {
            newArr[i] = data[i];
        }
        data = newArr;
    }
}
