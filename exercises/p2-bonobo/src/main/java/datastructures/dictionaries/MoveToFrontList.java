package datastructures.dictionaries;

import cse332.datastructures.containers.Item;
import cse332.interfaces.misc.DeletelessDictionary;

import java.util.Iterator;

/**
 * 1. The list is typically not sorted.
 * 2. Add new items to the front of the list.
 * 3. Whenever find is called on an item, move it to the front of the
 * list. This means you remove the node from its current position
 * and make it the first node in the list.
 * 4. You need to implement an iterator. The iterator SHOULD NOT move
 * elements to the front.  The iterator should return elements in
 * the order they are stored in the list, starting with the first
 * element in the list. When implementing your iterator, you should
 * NOT copy every item to another dictionary/list and return that
 * dictionary/list's iterator.
 */
public class MoveToFrontList<K, V> extends DeletelessDictionary<K, V> {
    private int size;
    private ListNode frontNode;

    public class ListNode extends Item<K, V> {
        ListNode next;
        K key;
        V val;

        ListNode(K ke, V value) {
            super(ke, value);
            this.next = null;
            this.key = ke;
            this.val = value;
        }
    }

    public MoveToFrontList() {
        this.size = 0;
    }

    public int size() {
        return this.size;
    }

    @Override
    public V insert(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        //if no unique keys, set new value to the front of the list
        ListNode newFront = new ListNode(key, value);
        if (this.size == 0) {
            this.frontNode = newFront;
            this.size++;
            return null;
        }
        if (find(key) != null) {
            newFront.next = this.frontNode.next;
            this.frontNode = newFront;
            return (V) this.frontNode.val;
        } else {
            newFront.next = this.frontNode;
            this.frontNode = newFront;
            this.size++;
        }
        return null;
    }

    @Override
    public V find(K key) {
        //if the request is not legit, throw exception
        if (key == null) {
            throw new IllegalArgumentException();
        }
        //if number of unique keys is 0, return null
        if (this.size == 0) {
            return null;
        }
        //set front node to first search node
        ListNode searchNode = this.frontNode;
        //previous node is set to null, but will change to search node after first search
        ListNode prevNode = null;
        //run loop while searchNode != null
        for (int i = 0; i < this.size; i++) {
            if (searchNode.key.equals(key)) {
                V desiredVal = searchNode.val;
                //perform lazy deletion on the found node
                if (prevNode != null) {
                    prevNode.next = searchNode.next;
                    searchNode.next = this.frontNode;
                    this.frontNode = searchNode;
                }
                //set up found node as front of list
                return desiredVal;
            }
            prevNode = searchNode;
            searchNode = searchNode.next;
        }
        return null;
    }

    @Override
    public Iterator<Item<K, V>> iterator() {
        return new FListIterator();
    }

    private class FListIterator implements Iterator{
        private ListNode current;
        public FListIterator() {
            if (MoveToFrontList.this.frontNode != null && MoveToFrontList.this.size > 0) {
                this.current = MoveToFrontList.this.frontNode;
            }
        }
        public boolean hasNext() {
            return this.current != null;
        }
        public Item<K, V> next() {
            Item<K, V> newValue = new Item<K, V>(this.current);
            this.current = this.current.next;
            return newValue;
        }
    }
}
