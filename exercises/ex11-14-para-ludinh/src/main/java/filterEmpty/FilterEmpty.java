package filterEmpty;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class FilterEmpty {
    static ForkJoinPool POOL = new ForkJoinPool();

    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns an array with the lengths of the non-empty strings from arr (in order)
     * For example, if arr is ["", "", "cse", "332", "", "hw", "", "7", "rox"], then
     * main.java.filterEmpty(arr) == [3, 3, 2, 1, 3].
     *
     * A parallel algorithm to solve this problem in O(lg n) span and O(n) work is the following:
     * (1) Do a parallel map to produce a bit set
     * (2) Do a parallel prefix over the bit set
     * (3) Do a parallel map to produce the output
     *
     * In lecture, we wrote parallelPrefix together, and it is included in the gitlab repository.
     * Rather than reimplementing that piece yourself, you should just use it. For the other two
     * parts though, you should write them.
     *
     * Do not bother with a sequential cutoff for this exercise, just have a base case that processes a single element.
     */

    public static int[] filterEmpty(String[] arr) {
        if (arr.length == 0) {
            return new int[0];
        }
        int[] bits = mapToBitSet(arr);

        int[] bitsum = ParallelPrefixSum.parallelPrefixSum(bits);

        return mapToOutput(arr, bits, bitsum);
    }

    public static int[] mapToBitSet(String[] arr) {
        /* TODO: Edit this with your code */
        int[] result = new int[arr.length];
        POOL.invoke(new MapToBitSetTask(arr, result, 0, arr.length));
        return result;
    }

    public static void bitSequential(String[] arr, int[] result, int lo, int hi) {
        if (arr[lo].length() > 0) {
            result[lo] = 1;
        }
    }

    /* TODO: Add a sequential method and parallel task here */
    public static class MapToBitSetTask extends RecursiveTask {
        String[] arr;
        int[] result;
        int lo, hi;

        public MapToBitSetTask(String[] arr, int[] result, int lo, int hi) {
            this.arr = arr;
            this.result = result;
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        protected Object compute() {
            if (hi - lo <= 1) {
                bitSequential(arr, result, lo, hi);
                return null;
            }

            int mid = lo + (hi - lo)/2;
            MapToBitSetTask left = new MapToBitSetTask(arr, result, lo, mid);
            MapToBitSetTask right = new MapToBitSetTask(arr, result, mid, hi);

            left.fork();
            right.compute();
            left.join();

            return null;
        }
    }

    public static int[] mapToOutput(String[] input, int[] bits, int[] bitsum) {
        /* TODO: Edit this with your code */
        int[] result = new int[bitsum[bitsum.length - 1]];
        POOL.invoke(new MapToOutputTask(input, bits, bitsum, result, 0, bitsum.length));
        return result;
    }

    /* TODO: Add a sequential method and parallel task here */
    public static void outputSequential(String[] input, int[] bits, int[] bitsum, int[] result, int lo, int hi) {
        if (lo == 0 && bitsum[lo] == 1) {
            result[lo] = input[lo].length();
        } else if (lo > 0 && bitsum[lo] > bitsum[lo - 1]) {
            result[bitsum[lo] - 1] = input[lo].length();
        }
    }

    public static class MapToOutputTask extends RecursiveTask {
        String[] input;
        int[] bits, bitsum, result;
        int lo, hi;

        public MapToOutputTask(String[] input, int[] bits, int[] bitsum, int[] result, int lo, int hi) {
            this.input = input;
            this.bits = bits;
            this.bitsum = bitsum;
            this.result = result;
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        protected Object compute() {
            if (hi - lo <= 1) {
                outputSequential(input, bits, bitsum, result, lo, hi);
                return null;
            }

            int mid = lo + (hi - lo)/2;
            MapToOutputTask left = new MapToOutputTask(input, bits, bitsum, result, lo, mid);
            MapToOutputTask right = new MapToOutputTask(input, bits, bitsum, result, mid, hi);

            left.fork();
            right.compute();
            left.join();

            return null;
        }
    }

    private static void usage() {
        System.err.println("USAGE: FilterEmpty <String array>");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
        }

        String[] arr = args[0].replaceAll("\\s*", "").split(",");
        System.out.println(Arrays.toString(filterEmpty(arr)));
    }
}