package queryresponders;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;
import paralleltasks.CornerFindingTask;
import paralleltasks.PopulateGridTask;

import java.util.concurrent.ForkJoinPool;

public class ComplexParallel extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    private final int[][] grid;

    public ComplexParallel(CensusGroup[] censusData, int numColumns, int numRows) {
        // Find the map corners and total population in parallel
        CornerFindingResult res = POOL.invoke(new CornerFindingTask(censusData, 0, censusData.length));
        MapCorners usCorners = res.getMapCorners();
        this.totalPopulation = res.getTotalPopulation();

        // Find grid which holds total population for each cell in parallel
        int[][] grid = POOL.invoke(new PopulateGridTask(censusData, 0, censusData.length,
                numRows, numColumns, usCorners,
                (usCorners.east - usCorners.west) / numColumns,
                (usCorners.north - usCorners.south) / numRows));

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
