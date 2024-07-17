package aboveandbeyond;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/* A class to run (array-based) parallel prefix sum on a single array of integers */
public class ParallelPrefixSumRow {
    private static final int CUTOFF = 100;
    private static final ForkJoinPool POOL = new ForkJoinPool();

    // Run array-based parallel prefix sum on an array
    public static void parallelPrefixSum(int[] row) {
        int sumsLen = (int) Math.pow(2, (Math.log(row.length) /  Math.log(2)) + 1);
        PSTNode[] sums = new PSTNode[sumsLen];
        POOL.invoke(new ProcessInputTaskRow(row, sums, 0, row.length, 0));
        POOL.invoke(new CreateOutputTaskRow(row, sums, 0, row.length, 0, 0));
    }

    private static int sumRow(int[] input, int lo, int hi) {
        int sum = 0;
        for (int i = lo; i < hi; i++) {
            sum += input[i];
        }
        return sum;
    }

    private static class ProcessInputTaskRow extends RecursiveAction {
        private final int[] input;
        private final PSTNode[] output;
        private final int lo;
        private final int hi;
        private final int index;

        public ProcessInputTaskRow(int[] input, PSTNode[] output, int lo, int hi, int index) {
            this.input = input;
            this.output = output;
            this.lo = lo;
            this.hi = hi;
            this.index = index;
        }

        @Override
        protected void compute() {
            if (hi - lo <= CUTOFF) {
                this.output[this.index] = new PSTNode(sumRow(this.input, this.lo, this.hi), this.lo, this.hi);
                return;
            }

            int mid = (this.hi + this.lo) / 2;
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;
            ProcessInputTaskRow leftTask = new ProcessInputTaskRow(input, output, this.lo, mid, leftChild);
            ProcessInputTaskRow rightTask = new ProcessInputTaskRow(input, output, mid, this.hi, rightChild);
            leftTask.fork();
            rightTask.compute();
            leftTask.join();
            this.output[index] = new PSTNode(this.output[leftChild].sum + this.output[rightChild].sum, this.lo, this.hi);
        }
    }

    private static class CreateOutputTaskRow extends RecursiveAction {
        private final int[] input;
        private final PSTNode[] sums;
        private final int hi;
        private final int lo;
        private final int index;
        private final int prescan;

        public CreateOutputTaskRow(int[] input, PSTNode[] sums, int lo, int hi, int index, int prescan) {
            this.input = input;
            this.sums = sums;
            this.hi = hi;
            this.lo = lo;
            this.index = index;
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
                    sum += input[i];
                    input[i] = sum;
                }
                return;
            }
            int mid = (this.hi + this.lo) / 2;
            CreateOutputTaskRow left = new CreateOutputTaskRow(this.input, this.sums,
                    this.lo, mid, leftChild, this.prescan);
            CreateOutputTaskRow right = new CreateOutputTaskRow(this.input, this.sums,
                    mid, this.hi, rightChild, this.prescan + this.sums[leftChild].sum);
            left.fork();
            right.compute();
            left.join();
        }
    }
}
