package datastructures.worklists;
import java.util.NoSuchElementException;

//import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.worklists.FIFOWorkList;

/**
 * See cse332/interfaces/worklists/FIFOWorkList.java
 * for method specifications.
 */
public class ListFIFOQueue<E> extends FIFOWorkList<E> {
    private ListFIFOQueueNode front;
    private ListFIFOQueueNode back;
    private int size;

    public ListFIFOQueue() {
        front = null;
        back = null;
        size = 0;
    }

    @Override
    public void add(E work) {
        ListFIFOQueueNode newNode = new ListFIFOQueueNode(work);
        if (size == 0){
            front = newNode;
            back = newNode;
        } else {
            back.next = newNode;
            back = newNode;
        }
        size++;
    }

    @Override
    public E peek() {
        if (!hasWork()) {
            throw new NoSuchElementException();
        }
        return front.data;
    }

    @Override
    public E next() {
        if (!hasWork()) {
            throw new NoSuchElementException();
        }

        E data = front.data;
        if (size == 1) {
            front = null;
            back = null;
        } else {
            front = front.next;
        }
        size--;
        return data;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        front = null;
        back = null;
        size = 0;
    }

    private class ListFIFOQueueNode {
        private E data;
        private ListFIFOQueueNode next;

        public ListFIFOQueueNode(E data) {
            this.data = data;
            this.next = null;
        }
    }
}
