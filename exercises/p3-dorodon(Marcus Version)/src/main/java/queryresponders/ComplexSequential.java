package queryresponders;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;


public class ComplexSequential extends QueryResponder {
    private final int[][] grid;

    public ComplexSequential(CensusGroup[] censusData, int numColumns, int numRows) {
        int[][] grid = new int[numRows + 1][numColumns + 1];

        // Get map corners, total population
        MapCorners usCorners = new MapCorners(censusData[0]);
        this.totalPopulation = censusData[0].population;
        for(int i = 1; i < censusData.length; i++) {
            CensusGroup curr = censusData[i];

            usCorners = usCorners.encompass(new MapCorners(curr));
            this.totalPopulation += curr.population;
        }

        // Update grid with populations
        double colSize = (usCorners.east - usCorners.west) / numColumns; // Width of our columns
        double rowSize = (usCorners.north - usCorners.south) / numRows; // Height of our rows
        for(int i = 0; i < censusData.length; i++) {
            CensusGroup curr = censusData[i];

            // Column which curr falls into (1-based)
            int col = Math.min(numColumns, (int)((curr.longitude - usCorners.west) / colSize) + 1);
            // Row which curr falls into (1-based)
            int row = Math.min(numRows, (int)((curr.latitude - usCorners.south) / rowSize) + 1);

            grid[row][col] += curr.population;
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
