package queryresponders;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;
import paralleltasks.CornerFindingTask;
import paralleltasks.PopulateGridTask;

import java.util.concurrent.ForkJoinPool;

public class ComplexParallel extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    private CornerFindingTask cornerTask;
    private CornerFindingResult cornerResults;
    private CensusGroup[] censusData;
    private int numColumns;
    private int numRows;
    private MapCorners corners;
    private int[][] grid;

    public ComplexParallel(CensusGroup[] censusData, int numColumns, int numRows) {
        this.censusData = censusData;
        this.numColumns = numColumns;
        this.numRows = numRows;
        cornerTask = new CornerFindingTask(censusData, 0, censusData.length);
        cornerTask.fork();
        cornerResults = cornerTask.join();
        this.corners = cornerResults.getMapCorners();
        this.totalPopulation = cornerResults.getTotalPopulation();

        double cellWidth = (corners.east - corners.west) / this.numColumns;
        double cellHeight = (corners.north - corners.south) / this.numRows;
        PopulateGridTask gridTask = new PopulateGridTask(censusData, 0, censusData.length,
                this.numRows, this.numColumns, this.corners, cellWidth, cellHeight);
        gridTask.fork();
        this.grid = gridTask.join();
        quickSum();
    }

    private void quickSum() {
        for (int i = 1; i < numRows + 1; i++) {
            for (int j = 1; j < numColumns + 1; j++) {
                this.grid[i][j] = this.grid[i][j] + this.grid[i - 1][j]
                        + this.grid[i][j - 1] - this.grid[i - 1][j - 1];
            }
        }
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        if (west < 1 || west > numColumns || south < 1 || south > numRows ||
                east < west || east > numColumns || north < south || north > numRows) {
            throw new IllegalArgumentException();
        }
        return grid[north][east] - grid[south - 1][east] - grid[north][west - 1] + grid[south - 1][west - 1];
    }
}
