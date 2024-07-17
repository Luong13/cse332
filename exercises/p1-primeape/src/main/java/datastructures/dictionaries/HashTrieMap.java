package datastructures.dictionaries;

import cse332.exceptions.NotYetImplementedException;
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
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        HashTrieNode current = (HashTrieNode) this.root;
        for (A character : key) {
            if (!current.pointers.containsKey(character)) {
                current.pointers.put(character, new HashTrieNode());
            }
            current = current.pointers.get(character);
        }
        V val = current.value;
        current.value = value;
        if (val == null) {
            size++;
        }
        return val;
    }

    @Override
    public V find(K key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        HashTrieNode current = (HashTrieNode) this.root;
        for (A character : key) {
            if (current.pointers.containsKey(character)) {
                current = current.pointers.get(character);
            } else {
                return null;
            }
        }
        return current.value;
    }

    @Override
    public boolean findPrefix(K key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        HashTrieNode current = (HashTrieNode) this.root;
        for (A character : key) {
            if (current.pointers.containsKey(character)) {
                current = current.pointers.get(character);
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void delete(K key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        boolean exists = true;
        HashTrieNode current = (HashTrieNode) this.root;
        HashTrieNode checkpoint = null;
        A ckptChar = null;
        for (A character : key) {
            if (current.pointers.containsKey(character)) {
                if (current.value != null || current.pointers.size() > 1) {
                    checkpoint = current;
                    ckptChar = character;
                }
                current = current.pointers.get(character);
            } else {
                exists = false;
                break;
            }
        }
        if (exists) {
            current.value = null;
            if (current.pointers.isEmpty()) {
                if (checkpoint != null){
                    checkpoint.pointers.remove(ckptChar);
                } else {
                    this.root = new HashTrieNode();
                }
            }
            size--;
        }
    }

    @Override
    public void clear() {
        this.root = new HashTrieNode();
        size = 0;
    }
}
