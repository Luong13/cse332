package getLongestSequence;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class GetLongestSequence {
    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns the length of the longest consecutive sequence of val in arr.
     * For example, if arr is [2, 17, 17, 8, 17, 17, 17, 0, 17, 1], then
     * getLongestSequence(17, arr) == 3 and getLongestSequence(35, arr) == 0.
     *
     * Your code must have O(n) work, O(lg n) span, where n is the length of arr, and actually use the sequentialCutoff
     * argument. We have provided you with an extra class SequenceRange. We recommend you use this class as
     * your return value, but this is not required.
     */
    public static final ForkJoinPool POOL = new ForkJoinPool();

    public static int getLongestSequence(int val, int[] arr, int sequentialCutoff) {
        /* TODO: Edit this with your code */
        return POOL.invoke(new GetLongestSequenceTask(arr, 0, arr.length, val, sequentialCutoff)).longestRange;
    }

    /* TODO: Add a sequential method and parallel task here */
    public static SequenceRange sequential(int[] arr, int lo, int hi, int val) {
        SequenceRange sequenceRange = new SequenceRange(0, 0, 0, hi - lo);
        int longestRange = 0;
        int i = lo;
        while (i < hi && arr[i] == val) {
            longestRange++;
            i++;
            if (i == hi) {
                sequenceRange.matchingOnRight = longestRange;
            }
        }
        sequenceRange.matchingOnLeft = longestRange;

        for (; i < hi; i++) {
            if (arr[i] == val) {
                int j = i;
                int count = 0;
                while (j < hi && arr[j] == val) {
                    count++;
                    j++;
                    if (j == hi) {
                        sequenceRange.matchingOnRight = count;
                    }
                }
                longestRange = Math.max(longestRange, count);
                i = j ;
            }

        }
        sequenceRange.longestRange = longestRange;
        return sequenceRange;
    }

    public static class GetLongestSequenceTask extends RecursiveTask<SequenceRange> {
        int[] arr;
        int lo, hi;
        int val, cutoff;

        public GetLongestSequenceTask(int[] arr, int lo, int hi, int val, int cutoff) {
            this.arr = arr;
            this.lo = lo;
            this.hi = hi;
            this.val = val;
            this.cutoff = cutoff;
        }

        @Override
        protected SequenceRange compute() {
            if (hi - lo <= cutoff) {
                return sequential(arr, lo, hi, val);
            }

            int mid = lo + (hi - lo)/2;
            GetLongestSequenceTask left = new GetLongestSequenceTask(arr, lo, mid, val, cutoff);
            GetLongestSequenceTask right = new GetLongestSequenceTask(arr, mid, hi, val, cutoff);

            left.fork();
            SequenceRange rightAns = right.compute();
            SequenceRange leftAns = left.join();

            int compare = Math.max(leftAns.longestRange, rightAns.longestRange);
            int combine = leftAns.matchingOnRight + rightAns.matchingOnLeft;
            int longestRange = Math.max(compare, combine);

            int matchingLeft = leftAns.matchingOnLeft;
            int matchingRight = rightAns.matchingOnRight;

            if (leftAns.matchingOnLeft == leftAns.sequenceLength) {
                matchingLeft += rightAns.matchingOnLeft;
            }
            if (rightAns.matchingOnRight == rightAns.sequenceLength) {
                matchingRight += leftAns.matchingOnRight;
            }

            SequenceRange result = new SequenceRange(matchingLeft, matchingRight, longestRange, hi - lo);

            return result;
        }
    }

    private static void usage() {
        System.err.println("USAGE: GetLongestSequence <number> <array> <sequential cutoff>");
        System.exit(2);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            usage();
        }

        int val = 0;
        int[] arr = null;

        try {
            val = Integer.parseInt(args[0]);
            String[] stringArr = args[1].replaceAll("\\s*", "").split(",");
            arr = new int[stringArr.length];
            for (int i = 0; i < stringArr.length; i++) {
                arr[i] = Integer.parseInt(stringArr[i]);
            }
            System.out.println(getLongestSequence(val, arr, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }
    }
}