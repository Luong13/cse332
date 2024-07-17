package aboveandbeyond;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/* A class to run (array-based) parallel prefix sum on a given column of a 2-dimensional array */
public class ParallelPrefixSumCol {
    private static final int CUTOFF = 100;
    private static final ForkJoinPool POOL = new ForkJoinPool();

    // Run parallel prefix sum on a column of a given array
    public static void parallelPrefixSum(int[][] grid, int col) {
        int sumsLen = (int) Math.pow(2, (Math.log(grid.length) /  Math.log(2)) + 1);
        PSTNode[] sums = new PSTNode[sumsLen];
        POOL.invoke(new ProcessInputTaskCol(grid, sums, 0, grid.length, col, 0));
        POOL.invoke(new CreateOutputTaskCol(grid, sums, 0, grid.length, 0, col, 0));
    }

    private static int sumCol(int[][] input, int lo, int hi, int col) {
        int sum = 0;
        for(int i = lo; i < hi; i++) {
            sum += input[i][col];
        }
        return sum;
    }

    private static class ProcessInputTaskCol extends RecursiveAction {
        private final int[][] input;
        private final PSTNode[] output;
        private final int lo;
        private final int hi;
        private final int col;
        private final int index;

        public ProcessInputTaskCol(int[][] input, PSTNode[] output, int lo, int hi, int col, int index) {
            this.input = input;
            this.output = output;
            this.lo = lo;
            this.hi = hi;
            this.col = col;
            this.index = index;
        }

        @Override
        protected void compute() {
            if (hi - lo <= CUTOFF) {
                this.output[this.index] = new PSTNode(sumCol(this.input, this.lo, this.hi, this.col), this.lo, this.hi);
                return;
            }

            int mid = (this.hi + this.lo) / 2;
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;
            ProcessInputTaskCol leftTask = new ProcessInputTaskCol(input, output, this.lo, mid, this.col, leftChild);
            ProcessInputTaskCol rightTask = new ProcessInputTaskCol(input, output, mid, this.hi, this.col, rightChild);
            leftTask.fork();
            rightTask.compute();
            leftTask.join();
            this.output[index] = new PSTNode(this.output[leftChild].sum + this.output[rightChild].sum, this.lo, this.hi);
        }
    }

    private static class CreateOutputTaskCol extends RecursiveAction {
        private final int[][] input;
        private final PSTNode[] sums;
        private final int hi;
        private final int lo;
        private final int index;
        private final int col;
        private final int prescan;

        public CreateOutputTaskCol(int[][] input, PSTNode[] sums, int lo, int hi, int index, int col, int prescan) {
            this.input = input;
            this.sums = sums;
            this.hi = hi;
            this.lo = lo;
            this.index = index;
            this.col = col;
            this.prescan = prescan;
        }

        @Override
        protected void compute() {
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;
            if (rightChild >= this.sums.length || this.sums[rightChild] == null) {
                int sum = this.prescan;
                PSTNode current = this.sums[index];
                for (int i = current.lo; i < current.hi; i++) {
                    sum += input[i][col];
                    input[i][col] = sum;
                }
                return;
            }
            int mid = (this.hi + this.lo) / 2;
            CreateOutputTaskCol left = new CreateOutputTaskCol(this.input, this.sums,
                    this.lo, mid, leftChild, this.col, this.prescan);
            CreateOutputTaskCol right = new CreateOutputTaskCol(this.input, this.sums,
                    mid, this.hi, rightChild, this.col, this.prescan + this.sums[leftChild].sum);
            left.fork();
            right.compute();
            left.join();
        }
    }
}
