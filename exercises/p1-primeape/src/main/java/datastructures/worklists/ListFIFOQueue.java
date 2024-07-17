package datastructures.worklists;

import cse332.interfaces.worklists.FIFOWorkList;

import java.util.NoSuchElementException;

/**
 * See cse332/interfaces/worklists/FIFOWorkList.java
 * for method specifications.
 */
public class ListFIFOQueue<E> extends FIFOWorkList<E> {
    private ListFIFOQueueNode head;
    private ListFIFOQueueNode tail;
    private int size;

    public ListFIFOQueue() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public void add(E work) {
        if (size() == 0) {
            ListFIFOQueueNode node = new ListFIFOQueueNode(work, null);
            head = node;
            tail = node;
        } else {
            ListFIFOQueueNode currentHead = head;
            ListFIFOQueueNode futureHead = new ListFIFOQueueNode(work, currentHead);
            currentHead.setPrev(futureHead);
            head = futureHead;
        }
        size++;
    }

    @Override
    public E peek() {
        if (!hasWork()) {
            throw new NoSuchElementException();
        }
        return tail.getData();
    }

    @Override
    public E next() {
        if (!hasWork()) {
            throw new NoSuchElementException();
        }

        E data;
        if (size == 1) {
            data = head.getData();
            head = null;
            tail = null;
        } else {
            ListFIFOQueueNode currentTail = tail;
            data = currentTail.getData();
            ListFIFOQueueNode futureTail = currentTail.getPrev();
            futureTail.setNext(null);
            tail = futureTail;
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
        head = null;
        tail = null;
        size = 0;
    }

    private class ListFIFOQueueNode {
        private final E data;
        private ListFIFOQueueNode next;
        private ListFIFOQueueNode prev;

        public ListFIFOQueueNode(E data, ListFIFOQueueNode next) {
            this.data = data;
            this.next = next;
            this.prev = null;
        }

        public E getData() {
            return data;
        }

        public ListFIFOQueueNode getPrev() {
            return prev;
        }

        public void setPrev(ListFIFOQueueNode prev) {
            this.prev = prev;
        }

        public void setNext(ListFIFOQueueNode next) {
            this.next = next;
        }
    }
}
