package datastructures.dictionaries;

import cse332.datastructures.containers.Item;
import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.misc.DeletelessDictionary;
import cse332.interfaces.misc.Dictionary;

import java.util.Iterator;
import java.util.function.Supplier;

/**
 * 1. You must implement a generic chaining hashtable. You may not
 * restrict the size of the input domain (i.e., it must accept
 * any key) or the number of inputs (i.e., it must grow as necessary).
 * 3. Your HashTable should rehash as appropriate (use load factor as
 * shown in class!).
 * 5. HashTable should be able to resize its capacity to prime numbers for more
 * than 200,000 elements. After more than 200,000 elements, it should
 * continue to resize using some other mechanism.
 * 6. We suggest you hard code some prime numbers. You can use this
 * list: http://primes.utm.edu/lists/small/100000.txt
 * NOTE: Do NOT copy the whole list!
 * 7. When implementing your iterator, you should NOT copy every item to another
 * dictionary/list and return that dictionary/list's iterator.
 */
public class ChainingHashTable<K, V> extends DeletelessDictionary<K, V> {
    private final int[] primesList = {389, 787, 1579, 3163, 6329, 12659, 25321, 50647, 101323, 202661};
    private int size;
    private int tableSize;
    //primesIndex holds the index of which prime the size of the array is currently on
    private int primesIndex;
    private Dictionary<K, V>[] hashTable;
    private Supplier<Dictionary<K, V>> newChain;

    public ChainingHashTable(Supplier<Dictionary<K, V>> newChain) {
        this.newChain = newChain;
        //initialize generic dictionary array
        hashTable = new Dictionary[this.primesList[0]];
        this.primesIndex = 0;
        this.size = 0;
        this.tableSize = hashTable.length;
    }

    @Override
    public V insert(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        //resize array if the load factor reaches over 1
        if (this.size > this.tableSize) {
            //two cases: first if the size is less than approx. 200,000, second for greater than
            if (primesIndex < primesList.length) {
                primesIndex++;
                tableSize = primesList[primesIndex];
            } else {
                tableSize = tableSize * 2;
            }
            Dictionary[] tempTable = new Dictionary[tableSize];
            for (int i = 0; i < hashTable.length; i++) {
                Iterator<Item<K, V>> itr = hashTable[i].iterator();
                while (itr.hasNext()) {
                    Item<K, V> work = itr.next();
                    K k = work.key;
                    V v = work.value;
                    int index = k.hashCode() % tableSize;
                    tempTable[index].insert(k, v);
                }
            }
            this.hashTable = tempTable;
        }
        int hashIndex = fitHashCodetoSize(key.hashCode());
        if (hashTable[hashIndex] == null) {
            hashTable[hashIndex] = this.newChain.get();
        }
        this.size++;
        //return result of the dictionary's own insert function
        return hashTable[hashIndex].insert(key, value);
    }

    @Override
    public V find(K key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        int hashIndex = fitHashCodetoSize(key.hashCode());
        return hashTable[hashIndex].find(key);
    }

    @Override
    public Iterator<Item<K, V>> iterator() {
        return (Iterator<Item<K, V>>) new CHTIterator(this);
    }

    private class CHTIterator implements Iterator {
        private Item<K, V> current;
        private Dictionary<K, V> dict;

        public CHTIterator(Dictionary<K, V> dict) {
            this.dict = dict;
            if (dict.size() > 0) {
                current = this.dict.iterator().next();
            }
        }

        @Override
        public boolean hasNext() {
            return this.dict.iterator().hasNext();
        }

        @Override
        public Object next() {
            return this.dict.iterator().next();
        }
    }

    /*
    * This function makes the index of the hash code small enough to fit in the current array size
    * */
    public int fitHashCodetoSize(int hash) {
        return hash % this.tableSize;
    }
}
