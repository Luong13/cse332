package experiment;
import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import main.PopulationQuery;
import queryresponders.ComplexSequential;
import queryresponders.SimpleSequential;

import java.util.Random;

public class ExperimentMain {
    private static final int NUM_WARMUP = 2;
    private static final int NUM_TESTS = 50;
    private static final int ROWS = 100;
    private static final int COLS = 500;
    private static final CensusGroup[] DATA =  PopulationQuery.parse("CenPop2010.txt");

    public static void main(String[] args) {
        int[] ns = new int[20];
        for(int i  = 1; i <= 20; i++) {
            ns[i - 1] = i;
        }

        for(int i = 0; i < ns.length; i++) {
            System.out.println(ns[i] + ":");
            Query[] queries = new Query[ns[i]];
            for (int j = 0; j < queries.length; j++) {
                Random r = new Random();
                int w = r.nextInt(COLS - 1) + 1;
                int s = r.nextInt(ROWS - 1) + 1;
                int e = r.nextInt(COLS - w) + w;
                int n = r.nextInt(ROWS - s) + s;
                queries[j] = new Query(w, s, e, n);
            }
            runExperiment(queries);
        }
    }

    private static void runExperiment(Query[] queries) {
        double totalTime = 0;
        QueryResponder simple;
        for (int i = 0; i < NUM_TESTS; i++) {
            long startTime = System.nanoTime();
            simple = new SimpleSequential(DATA, COLS, ROWS);
            for(int j = 0; j < queries.length; j++) {
                Query curr = queries[j];
                simple.getPopulation(curr.w, curr.s, curr.e, curr.n);
            }
            long endTime = System.nanoTime();
            // Throw away first NUM_WARMUP runs to exclude JVM warmup
            if (NUM_WARMUP <= i) {
                totalTime += (endTime - startTime);
            }
        }
        double averageRuntime = totalTime / (NUM_TESTS - NUM_WARMUP);
        System.out.println("Total Average Runtime (Simple): " + averageRuntime);

        totalTime = 0;
        QueryResponder complex;
        for (int i = 0; i < NUM_TESTS; i++) {
            long startTime = System.nanoTime();
            complex = new ComplexSequential(DATA, COLS, ROWS);
            for(int j = 0; j < queries.length; j++) {
                Query curr = queries[j];
                complex.getPopulation(curr.w, curr.s, curr.e, curr.n);
            }
            long endTime = System.nanoTime();
            // Throw away first NUM_WARMUP runs to exclude JVM warmup
            if (NUM_WARMUP <= i) {
                totalTime += (endTime - startTime);
            }
        }
        averageRuntime = totalTime / (NUM_TESTS - NUM_WARMUP);
        System.out.println("Total Average Runtime (Complex): " + averageRuntime);
    }

    private static class Query {
        int w;
        int s;
        int e;
        int n;

        public Query(int w, int s, int e, int n) {
            this.w = w;
            this.s = s;
            this.e = e;
            this.n = n;
        }
    }
}
