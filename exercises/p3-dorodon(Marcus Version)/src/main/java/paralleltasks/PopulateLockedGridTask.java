package paralleltasks;

import cse332.types.CensusGroup;
import cse332.types.MapCorners;
import java.util.concurrent.locks.Lock;

/*
   1) This class is used in version 5 to create the initial grid holding the total population for each grid cell
        - You should not be using the ForkJoin framework but instead should make use of threads and locks
        - Note: the resulting grid after all threads have finished running should be the same as the final grid from
          PopulateGridTask.java
 */

public class PopulateLockedGridTask extends Thread {
    CensusGroup[] censusGroups;
    int lo, hi, numRows, numColumns;
    MapCorners corners;
    double cellWidth, cellHeight;
    int[][] populationGrid;
    Lock[][] lockGrid;

    public PopulateLockedGridTask(CensusGroup[] censusGroups, int lo, int hi, int numRows, int numColumns, MapCorners corners,
                                  double cellWidth, double cellHeight, int[][] popGrid, Lock[][] lockGrid) {
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.corners = corners;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.populationGrid = popGrid;
        this.lockGrid = lockGrid;
    }

    @Override
    public void run() {
        for(int i = this.lo; i < this.hi; i++) {
            CensusGroup curr = this.censusGroups[i];

            // Column which curr falls into (1-based)
            int col = Math.min(numColumns, (int)((curr.longitude - this.corners.west) / this.cellWidth) + 1);
            // Row which curr falls into (1-based)
            int row = Math.min(numRows, (int)((curr.latitude - this.corners.south) / this.cellHeight) + 1);

            // Acquire lock before writing to populationGrid, release immediately afterwards
            this.lockGrid[row][col].lock();
            populationGrid[row][col] += curr.population;
            this.lockGrid[row][col].unlock();
        }
    }
}
