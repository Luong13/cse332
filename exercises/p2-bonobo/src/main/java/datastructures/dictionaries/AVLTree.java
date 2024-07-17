package datastructures.dictionaries;

import cse332.datastructures.trees.BinarySearchTree;

/**
 * AVLTree must be a subclass of BinarySearchTree<E> and must use
 * inheritance and calls to superclass methods to avoid unnecessary
 * duplication or copying of functionality.
 * <p>
 * 1. Create a subclass of BSTNode, perhaps named AVLNode.
 * 2. Override the insert method such that it creates AVLNode instances
 * instead of BSTNode instances.
 * 3. Do NOT "replace" the children array in BSTNode with a new
 * children array or left and right fields in AVLNode.  This will
 * instead mask the super-class fields (i.e., the resulting node
 * would actually have multiple copies of the node fields, with
 * code accessing one pair or the other depending on the type of
 * the references used to access the instance).  Such masking will
 * lead to highly perplexing and erroneous behavior. Instead,
 * continue using the existing BSTNode children array.
 * 4. Ensure that the class does not have redundant methods
 * 5. Cast a BSTNode to an AVLNode whenever necessary in your AVLTree.
 * This will result a lot of casts, so we recommend you make private methods
 * that encapsulate those casts.
 * 6. Do NOT override the toString method. It is used for grading.
 * 7. The internal structure of your AVLTree (from this.root to the leaves) must be correct
 */

public class AVLTree<K extends Comparable<? super K>, V> extends BinarySearchTree<K, V> {
    // TODO: Implement me!

    public AVLTree() {
        super();
    }

    public class AVLNode extends BSTNode {
        public BinarySearchTree<K, V>.BSTNode[] children;
        public int height;

        /**
         * Create a new data node.
         *
         * @param key   key with which the specified value is to be associated
         * @param value
         */
        public AVLNode(K key, V value) {
            super(key, value);
            this.children = super.children;
            this.height = 0;
        }
    }

    public V insert(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }

        if (this.root == null) {
            this.root = new AVLNode(key, value);
            size++;
            return null;
        }

        V oldValue = find(key);

        this.root = insert((AVLNode)this.root, key, value);

        return oldValue;
    }

    private AVLNode insert(AVLNode current, K key, V value) {
        if (current == null) {
            this.size++;
            return new AVLNode(key, value);
        }

        int dir = Integer.signum(key.compareTo(current.key));
        if (dir == -1) {
            current.children[0] = insert((AVLNode)current.children[0], key, value);
        } else if (dir == 1) {
            current.children[1] = insert((AVLNode)current.children[1], key, value);
        } else {
            current.value = value;
            return current;
        }

        current.height = Math.max(height((AVLNode)current.children[0]),
                height((AVLNode)current.children[1])) + 1;

        int balance = height((AVLNode)current.children[0]) -
                height((AVLNode)current.children[1]);

        if (balance > 1) { //left subtree height too high
            if (Integer.signum(key.compareTo(current.children[0].key)) < 0) { //inserted in left left subtree
                return rotate(current, 1);
            } else { //inserted in left right subtree
                current.children[0] = rotate((AVLNode)current.children[0], 0);
                return rotate(current, 1);
            }
        }
        if (balance < -1) { //right subtree height too high
            if (Integer.signum(key.compareTo(current.children[1].key)) > 0) { //inserted in right right subtree
                return rotate(current, 0);
            } else { //inserted in right left subtree
                current.children[1] = rotate((AVLNode)current.children[1], 1);
                return rotate(current, 0);
            }
        }
        return current;
    }

    private AVLNode rotate(AVLNode current, int dir) {
        AVLNode temp = (AVLNode)current.children[1 - dir];
        current.children[1 - dir] = temp.children[dir];
        temp.children[dir] = current;
        current.height = Math.max(height((AVLNode)current.children[0]),
                height((AVLNode)current.children[1])) + 1;
        temp.height = Math.max(height((AVLNode)temp.children[0]),
                height((AVLNode)temp.children[1])) + 1;
        return temp;
    }

    private int height(AVLNode x) {
        if (x == null) {
            return -1;
        }
        return x.height;
    }
}