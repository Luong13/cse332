package paralleltasks;

import cse332.types.CensusGroup;
import cse332.types.MapCorners;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
/*
   1) This class is used in version 4 to create the initial grid holding the total population for each grid cell
   2) SEQUENTIAL_CUTOFF refers to the maximum number of census groups that should be processed by a single parallel task
   3) Note that merging the grids from the left and right subtasks should NOT be done in this class.
      You will need to implement the merging in parallel using a separate parallel class (MergeGridTask.java)
 */

public class PopulateGridTask extends RecursiveTask<int[][]> {
    final static int SEQUENTIAL_CUTOFF = 10000;
    private static final ForkJoinPool POOL = new ForkJoinPool();
    CensusGroup[] censusGroups;
    int lo, hi, numRows, numColumns;
    MapCorners corners;
    double cellWidth, cellHeight;

    public PopulateGridTask(CensusGroup[] censusGroups, int lo, int hi, int numRows, int numColumns,
                            MapCorners corners, double cellWidth, double cellHeight) {
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.corners = corners;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
    }

    @Override
    protected int[][] compute() {
        if(this.hi - this.lo <= SEQUENTIAL_CUTOFF) {
            return this.sequentialPopulateGrid();
        }
        PopulateGridTask left = new PopulateGridTask(this.censusGroups, this.lo, (this.lo + this.hi) / 2,
                numRows, numColumns,
                corners, cellWidth, cellHeight);
        PopulateGridTask right = new PopulateGridTask(this.censusGroups,(this.lo + this.hi) / 2, this.hi,
                numRows, numColumns,
                corners, cellWidth, cellHeight);
        left.fork();
        int[][] rightAns = right.compute();
        int[][] leftAns = left.join();

        // Merges right grid into left grid in parallel
        POOL.invoke(new MergeGridTask(leftAns, rightAns, 1, this.numRows + 1, 1, this.numColumns + 1));
        return leftAns;
    }

    private int[][] sequentialPopulateGrid() {
        int[][] grid = new int[this.numRows + 1][this.numColumns + 1];

        for(int i = this.lo; i < this.hi; i++) {
            CensusGroup curr = this.censusGroups[i];

            // Column which curr falls into (1-based)
            int col = Math.min(numColumns, (int)((curr.longitude - this.corners.west) / this.cellWidth) + 1);
            // Row which curr falls into (1-based)
            int row = Math.min(numRows, (int)((curr.latitude - this.corners.south) / this.cellHeight) + 1);

            grid[row][col] += curr.population;
        }
        return grid;
    }
}

