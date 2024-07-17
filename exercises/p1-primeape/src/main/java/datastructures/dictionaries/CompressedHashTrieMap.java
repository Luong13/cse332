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
public class CompressedHashTrieMap<A extends Comparable<A>, K extends BString<A>, V> extends TrieMap<A, K, V> {
    private class CompressedHashTrieNode extends TrieNode<Map<A, CompressedHashTrieNode>, CompressedHashTrieNode> {
        public CompressedHashTrieNode() {
            this(null);
        }

        public CompressedHashTrieNode(V value) {
            this.pointers = new HashMap<A, CompressedHashTrieNode>();
            this.value = value;
        }

        @Override
        public Iterator<Entry<A, CompressedHashTrieMap<A, K, V>.CompressedHashTrieNode>> iterator() {
            return pointers.entrySet().iterator();
        }
    }

    private CompressedHashTrieNode root;

    public CompressedHashTrieMap(Class<K> KClass) {
        super(KClass);
        this.root = new CompressedHashTrieNode();
    }

    @Override
    public V insert(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        CompressedHashTrieNode current = this.root;
        for (A c : key) {
            if (!current.pointers.containsKey(c)) {
                current.pointers.put(c, new CompressedHashTrieNode());
            }
            current = current.pointers.get(c);
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
        CompressedHashTrieNode current = this.root;
        for (A c : key) {
            if (current.pointers.containsKey(c)) {
                current = current.pointers.get(c);
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
        CompressedHashTrieNode current = this.root;
        for (A c : key) {
            if (current.pointers.containsKey(c)) {
                current = current.pointers.get(c);
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
        CompressedHashTrieNode current = this.root;
        CompressedHashTrieNode checkpoint = null;
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
                    this.root = new CompressedHashTrieNode();
                }
            }
            size--;
        }
    }

    public void compress(CompressedHashTrieNode compressPoint) {
        CompressedHashTrieNode current = compressPoint;
        CompressedHashTrieNode checkpoint = null;
        A ckpt = null;
        if (current.pointers.size() > 1) {
            for (K key : current.pointers) {
                compress(current);
            }
        } else if (current.pointers = 1) {
            current = current.pointers.
        }

    }

    @Override
    public void clear() {
        this.root = new CompressedHashTrieNode();
        size = 0;
    }
}

