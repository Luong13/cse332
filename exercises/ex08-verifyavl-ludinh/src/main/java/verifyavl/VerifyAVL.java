package verifyavl;

public class VerifyAVL {
    public static final int MIN = -9999;
    public static final int MAX = 9999;
    public static boolean verifyAVL(AVLNode root) {
        /* TODO: Edit this with your code */
        if (root == null) {
            return true;
        }
        return verifyAVLHelper(root, MIN, MAX);
    }
    /* TODO: Add private methods if you want (recommended) */
    public static boolean verifyAVLHelper(AVLNode root, int min, int max) {
        if (root.key < min || root.key > max) {
            return false;
        }
        if (root.left == null && root.right == null){
            return root.height == 0;
        }
        if (root.left == null) {
            if (root.height != 1) {
                return false;
            }
            return verifyAVLHelper(root.right, root.key, max);
        }
        if (root.right == null) {
            if (root.height != 1) {
                return false;
            }
            return verifyAVLHelper(root.left, min, root.key);
        }
        if (root.height != (Math.max(root.left.height, root.right.height) + 1) ||
        Math.abs(root.left.height - root.right.height) > 1) {
            return false;
        }
        return verifyAVLHelper(root.left, min, root.key) &&
                verifyAVLHelper(root.right, root.key, max);
    }
}