package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
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
        this.numColumns = numColumns;
        this.numRows = numRows;
        this.corners = corners;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
    }

    @Override
    protected int[][] compute() {
        if (hi - lo <= SEQUENTIAL_CUTOFF) {
            return sequentialPopulateGrid();
        }

        int mid = lo + (hi - lo)/2;
        PopulateGridTask left = new PopulateGridTask(censusGroups, lo, mid, numRows, numColumns, corners, cellWidth, cellHeight);
        PopulateGridTask right = new PopulateGridTask(censusGroups, mid, hi, numRows, numColumns, corners, cellWidth, cellHeight);

        left.fork();
        int[][] rightAns = right.compute();
        int[][] leftAns = left.join();


        MergeGridTask mergeTask = new MergeGridTask(leftAns, rightAns, 0, numRows + 1, 0, numColumns + 1);
        mergeTask.compute();


        return leftAns;
    }

    private int[][] sequentialPopulateGrid() {
        int[][] arr = new int[this.numRows + 1][this.numColumns + 1];
        for (int i = lo; i < hi; i++) {
            int xOffset = 0;
            int yOffset = 0;
            double xCoord = (censusGroups[i].longitude - this.corners.west) / cellWidth;
            double yCoord = (censusGroups[i].latitude - this.corners.south) / cellHeight;
            if (xCoord % 1 == 0 && xCoord < this.numColumns) {
                xOffset = 1;
            }
            if (yCoord % 1 == 0 && yCoord < this.numRows) {
                yOffset = 1;
            }
            int xBlock = Math.min((int) Math.ceil(xCoord) + xOffset, numColumns);
            int yBlock = Math.min((int) Math.ceil(yCoord) + yOffset, numRows);
            arr[yBlock][xBlock] += censusGroups[i].population;
        }
        return arr;
    }
}

