package datastructures.dictionaries;

import cse332.interfaces.trie.TrieMap;
import cse332.types.BString;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * See cse332/interfaces/trie/TrieMap.java
 * and cse332/interfaces/misc/Dictionary.java
 * for method specifications.
 */
public class HashTrieMap<A extends Comparable<A>, K extends BString<A>, V> extends TrieMap<A, K, V> {
    public class HashTrieNode extends TrieNode<Map<A, HashTrieNode>, HashTrieNode> {
        public HashTrieNode() {
            this(null);
        }

        public HashTrieNode(V value) {
            this.pointers = new HashMap<A, HashTrieNode>();
            this.value = value;
        }

        @Override
        public Iterator<Entry<A, HashTrieMap<A, K, V>.HashTrieNode>> iterator() {
            return pointers.entrySet().iterator();
        }
    }

    public HashTrieMap(Class<K> KClass) {
        super(KClass);
        this.root = new HashTrieNode();
    }

    @Override
    public V insert(K key, V value) {
        if(key == null || value == null) {
            throw new IllegalArgumentException();
        }

        HashTrieNode current = (HashTrieNode) this.root;
        Iterator<A> iterator = key.iterator();
        while(iterator.hasNext()) {
            A currLetter = iterator.next();

            // If we have a pointer to the next letter, move to that node and continue
            if(current.pointers.containsKey(currLetter)) {
                current = current.pointers.get(currLetter);
            } else {
                // Otherwise, create new node and move to it
                HashTrieNode newNode = new HashTrieNode();
                current.pointers.put(currLetter, newNode);
                current = newNode;
            }
        }
        // Set the new value
        V prevValue = current.value;
        current.value = value;
        this.size += prevValue == null ? 1 : 0;

        return prevValue;
    }

    @Override
    public V find(K key) {
        if(key == null) {
            throw new IllegalArgumentException();
        }

        HashTrieNode current = (HashTrieNode) this.root;
        Iterator<A> iterator = key.iterator();

        // Traverse trie until we reach end of trie or key
        while(iterator.hasNext()) {
            A currLetter = iterator.next();

            // If we reach dead end with more letters to go, key does not exist
            if(!current.pointers.containsKey(currLetter)) {
                return null;
            }
            current = current.pointers.get(currLetter);
        }
        return (V) current.value;
    }

    @Override
    public boolean findPrefix(K key) {
        if(key == null) {
            throw new IllegalArgumentException();
        }

        HashTrieNode current = (HashTrieNode) this.root;
        Iterator<A> iterator = key.iterator();

        // Traverse trie until we reach end or find defined key
        while(iterator.hasNext()) {
            A currentLetter = iterator.next();

            // If we reach dead end with more letters to go, prefix does not exist
            if(!current.pointers.containsKey(currentLetter)) {
                return false;
            }
            current = current.pointers.get(currentLetter);
        }
        // If we make it through the iterator, prefix exists
        return true;
    }

    @Override
    public void delete(K key) {
        throw new UnsupportedOperationException();
        /*if(key == null) {
            throw new IllegalArgumentException();
        }
        this.root = delete((HashTrieNode) this.root, key.iterator());

        // Ensure we don't end up in a state in which current is null
        if(this.root == null) this.root = new HashTrieNode();*/
    }

    private HashTrieNode delete(HashTrieNode current, Iterator<A> iterator) {
        if(current == null) {
            return null;
        }

        // If we make it to the last letter
        if(!iterator.hasNext()) {
            if(current.value != null) {
                current.value = null;
                this.size--;
            }

            if(current.pointers.isEmpty()) {
                current = null;
            }
            return current;
        }

        A currentLetter = iterator.next();
        if(current.pointers.containsKey(currentLetter)) {
            if(delete(current.pointers.get(currentLetter), iterator) == null) {
                current.pointers.remove(currentLetter);
            }
            // If we no longer need current node, delete it
            if(current.pointers.isEmpty() && current.value == null) {
                current = null;
            }
        }
        return current;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
        /*this.root = new HashTrieNode();
        this.size = 0;*/
    }
}