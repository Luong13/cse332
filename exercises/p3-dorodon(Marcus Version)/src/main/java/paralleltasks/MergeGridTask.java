package paralleltasks;

import java.util.concurrent.RecursiveAction;

/*
   1) This class is used by PopulateGridTask to merge two grids in parallel
   2) SEQUENTIAL_CUTOFF refers to the maximum number of grid cells that should be processed by a single parallel task
 */

public class MergeGridTask extends RecursiveAction {
    final static int SEQUENTIAL_CUTOFF = 10;
    int[][] left, right;
    int rowLo, rowHi, colLo, colHi;

    public MergeGridTask(int[][] left, int[][] right, int rowLo, int rowHi, int colLo, int colHi) {
        this.left = left;
        this.right = right;
        this.rowLo = rowLo;
        this.rowHi = rowHi;
        this.colLo = colLo;
        this.colHi = colHi;
    }

    @Override
    protected void compute() {
        if((this.rowHi - this.rowLo) * (this.colHi - this.colLo) <= SEQUENTIAL_CUTOFF) {
            this.sequentialMergeGird();
            return;
        }
        MergeGridTask bottomLeft = new MergeGridTask(this.left, this.right,
                this.rowLo, (this.rowLo + this.rowHi) / 2,
                this.colLo, (this.colLo + this.colHi) / 2);
        MergeGridTask bottomRight = new MergeGridTask(this.left, this.right,
                this.rowLo, (this.rowLo + this.rowHi) / 2,
                (this.colLo + this.colHi) / 2, this.colHi);
        MergeGridTask topLeft = new MergeGridTask(this.left, this.right,
                (this.rowLo + this.rowHi) / 2, this.rowHi,
                 this.colLo, (this.colLo + this.colHi) / 2);
        MergeGridTask topRight = new MergeGridTask(this.left, this.right,
                (this.rowLo + this.rowHi) / 2, this.rowHi,
                (this.colLo + this.colHi) / 2, this.colHi);
        bottomLeft.fork();
        bottomRight.fork();
        topLeft.fork();
        topRight.compute();
        bottomLeft.join();
        bottomRight.join();
        topLeft.join();
    }

    // according to google gird means "prepare oneself for something difficult or challenging" so this typo is intentional :)
    private void sequentialMergeGird() {
        for(int row = this.rowLo; row < this.rowHi; row++) {
            for(int col = this.colLo; col < this.colHi; col++) {
                this.left[row][col] += this.right[row][col];
            }
        }
    }
}
