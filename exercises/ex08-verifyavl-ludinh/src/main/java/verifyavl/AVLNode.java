package verifyavl;

/***
 * Do not modify anything here.
 */
public class AVLNode {
    public AVLNode left;
    public AVLNode right;
    public int key;
    public int height;

    public AVLNode(int key, int height, AVLNode left, AVLNode right) {
        this.key = key;
        this.height = height;
        this.left = left;
        this.right = right;
    }
}
