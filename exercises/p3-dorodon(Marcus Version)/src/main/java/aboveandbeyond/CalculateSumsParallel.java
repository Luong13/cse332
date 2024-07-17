package aboveandbeyond;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

// Class to apply array-based parallel prefix sum to all rows of matrix, then repeat on all columns of matrix
public class CalculateSumsParallel {
    private static final int CUTOFF = 100;
    private static final ForkJoinPool POOL = new ForkJoinPool();
    private static final int ROW = 0;
    private static final int COL = 1;

    public static void computeGrid(int[][] grid) {
        POOL.invoke(new PrefixSumTask(grid, 0, grid.length, ROW)); // Run parallel prefix sum on all rows
        POOL.invoke(new PrefixSumTask(grid, 0, grid[0].length, COL)); // Then, run parallel prefix sum on all columns
    }

    private static class PrefixSumTask extends RecursiveAction {
        private final int[][] grid;
        private final int lo;
        private final int hi;
        private final int computation;

        public PrefixSumTask(int[][] grid, int lo, int hi, int computation) {
            this.grid = grid;
            this.lo = lo;
            this.hi = hi;
            this.computation = computation;
        }

        @Override
        protected void compute() {
            if(this.hi - this.lo <= CUTOFF) {
                for(int i = this.lo; i < this.hi; i++) {
                    if(this.computation == ROW) {
                        ParallelPrefixSumRow.parallelPrefixSum(this.grid[i]);
                    } else {
                        ParallelPrefixSumCol.parallelPrefixSum(this.grid, i);
                    }
                }
                return;
            }
            PrefixSumTask left = new PrefixSumTask(this.grid, this.lo, (this.lo + this.hi) / 2, this.computation);
            PrefixSumTask right = new PrefixSumTask(this.grid, (this.lo + this.hi) / 2, this.hi, this.computation);
            left.fork();
            right.compute();
            left.join();
        }
    }
}