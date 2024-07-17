package aboveandbeyond;

/* Storage class for what we need to track during a parallel prefix sum */
public class PSTNode {
    int sum;
    int lo;
    int hi;

    public PSTNode(int sum, int lo, int hi) {
        this.sum = sum;
        this.lo = lo;
        this.hi = hi;
    }
}
