package queryresponders;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;
import paralleltasks.CornerFindingTask;
import paralleltasks.PopulateLockedGridTask;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ComplexLockBased extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool(); // only to invoke CornerFindingTask
    public int NUM_THREADS = 4;
    private final int[][] grid;

    public ComplexLockBased(CensusGroup[] censusData, int numColumns, int numRows) {
        // Compute corners and total population in parallel
        CornerFindingResult res = POOL.invoke(new CornerFindingTask(censusData, 0, censusData.length));
        this.totalPopulation = res.getTotalPopulation();
        MapCorners usCorners = res.getMapCorners();

        // If we are given more threads than we can use, only use what we can (1 thread per element)
        NUM_THREADS = Math.min(NUM_THREADS, censusData.length);

        // Instantiate grid and lock arrays such that there is a lock for every grid element (1-based)
        int[][] grid = new int[numRows + 1][numColumns + 1];
        Lock[][] locks = new Lock[numRows + 1][numColumns + 1];
        for(int row = 1; row < locks.length; row++) { // Populate lock array with reentrant locks
            for(int col = 1; col < locks[0].length; col++) {
                locks[row][col] = new ReentrantLock();
            }
        }

        // Initialize and start threads based on NUM_THREADS
        PopulateLockedGridTask[] threads = new PopulateLockedGridTask[NUM_THREADS - 1];
        int jump = (censusData.length / NUM_THREADS);
        for(int i = 0; i < NUM_THREADS - 1; i++) {
            threads[i] = new PopulateLockedGridTask(censusData,
                    jump * i, jump * (i + 1),
                    numRows, numColumns,
                    usCorners,
                    (usCorners.east - usCorners.west) / numColumns,
                    (usCorners.north - usCorners.south) / numRows,
                    grid, locks);
            threads[i].start();
        }

        PopulateLockedGridTask t = new PopulateLockedGridTask(censusData,
                jump * (NUM_THREADS - 1), censusData.length,
                numRows, numColumns,
                usCorners,
                (usCorners.east - usCorners.west) / numColumns,
                (usCorners.north - usCorners.south) / numRows,
                grid, locks);
        t.run();

        // Join threads
        try {
            for (int i = 0; i < threads.length; i++) {
                threads[i].join();
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Modify grid such that each cell holds itself + total population of all cells west and south of itself
        for(int row = 1; row <= numRows; row++) {
            for(int col = 1; col <= numColumns; col++) {
                int westward = grid[row][col - 1];
                int southward = grid[row - 1][col];
                int southwestward = grid[row - 1][col - 1];

                grid[row][col] = grid[row][col] + westward + southward - southwestward;
            }
        }
        this.grid = grid;
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        boolean invalidRect = west < 1 || west >= this.grid[0].length
                || south < 1 || south >= this.grid.length
                || east < west || east >= this.grid[0].length
                || north < south || north >= this.grid.length;

        if(invalidRect) {
            throw new IllegalArgumentException("Invalid rectangle");
        }

        int topRight = this.grid[north][east];
        int southBottomRight = this.grid[south - 1][east];
        int westTopLeft = this.grid[north][west - 1];
        int southWestBottomLeft = this.grid[south - 1][west - 1];

        return topRight - southBottomRight - westTopLeft + southWestBottomLeft;
    }
}
