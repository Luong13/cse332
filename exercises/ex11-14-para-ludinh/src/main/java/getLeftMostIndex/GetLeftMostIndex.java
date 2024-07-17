package getLeftMostIndex;

import getLongestSequence.GetLongestSequence;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class GetLeftMostIndex {
    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns the index of the left-most occurrence of needle in haystack (think of needle and haystack as
     * Strings) or -1 if there is no such occurrence.
     *
     * For example, main.java.getLeftMostIndex("cse332", "Dudecse4ocse332momcse332Rox") == 9 and
     * main.java.getLeftMostIndex("sucks", "Dudecse4ocse332momcse332Rox") == -1.
     *
     * Your code must actually use the sequentialCutoff argument. You may assume that needle.length is much
     * smaller than haystack.length. A solution that peeks across subproblem boundaries to decide partial matches
     * will be significantly cleaner and simpler than one that does not.
     */
    public static final ForkJoinPool POOL = new ForkJoinPool();

    public static int getLeftMostIndex(char[] needle, char[] haystack, int sequentialCutoff) {
        /* TODO: Edit this with your code */
        return POOL.invoke(new GetLeftMostIndexTask(needle, haystack, 0, haystack.length, sequentialCutoff));
    }

    /* TODO: Add a sequential method and parallel task here */
    public static int sequential(char[] needle, char[] haystack, int lo, int hi) {
        int index;
        boolean check;
        for (int i = lo; i < hi; i++) {
            if (haystack[i] == needle[0]) {
                int j = 0;
                index = i;
                check = true;
                while (i + j < haystack.length && check) {
                    if (haystack[i + j] == needle[j]) {
                        j++;
                        if (j >= needle.length) {
                            return index;
                        }
                    } else {
                        check = false;
                    }
                }
            }
        }

        return -1;
    }

    public static class GetLeftMostIndexTask extends RecursiveTask<Integer> {
        char[] needle;
        char[] haystack;
        int lo, hi;
        int cutoff;

        public GetLeftMostIndexTask(char[] needle, char[]haystack, int lo, int hi, int cutoff) {
            this.needle = needle;
            this.haystack = haystack;
            this.lo = lo;
            this.hi = hi;
            this.cutoff = cutoff;
        }

        @Override
        protected Integer compute() {
            if (hi - lo <= cutoff) {
                return sequential(needle, haystack, lo, hi);
            }

            int mid = lo + (hi - lo)/2;
            GetLeftMostIndexTask left = new GetLeftMostIndexTask(needle, haystack, lo, mid, cutoff);
            GetLeftMostIndexTask right = new GetLeftMostIndexTask(needle, haystack, mid, hi, cutoff);

            left.fork();
            int rightAns = right.compute();
            int leftAns = left.join();

            if (leftAns > -1 && rightAns > -1) {
                return Math.min(leftAns, rightAns);
            } else if (leftAns == -1) {
                return rightAns;
            } else {
                return leftAns;
            }
        }
    }

    private static void usage() {
        System.err.println("USAGE: GetLeftMostIndex <needle> <haystack> <sequential cutoff>");
        System.exit(2);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            usage();
        }

        char[] needle = args[0].toCharArray();
        char[] haystack = args[1].toCharArray();
        try {
            System.out.println(getLeftMostIndex(needle, haystack, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }
    }
}
