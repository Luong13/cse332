package queryresponders;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;


public class ComplexSequential extends QueryResponder {
    private CensusGroup[] censusData;
    private int numColumns;
    private int numRows;
    private MapCorners corners;
    private int[][] grid;

    public ComplexSequential(CensusGroup[] censusData, int numColumns, int numRows) {
        this.censusData = censusData;
        this.numColumns = numColumns;
        this.numRows = numRows;
        this.corners = new MapCorners(censusData[0]);
        this.totalPopulation = censusData[0].population;
        for (int i = 1; i < censusData.length; i++) {
            this.corners = this.corners.encompass(new MapCorners(censusData[i]));
            this.totalPopulation += censusData[i].population;
        }

        this.grid = computeGrid();
        quickSum();
    }

    private int[][] computeGrid() {
        int [][] arr = new int[this.numRows + 1][this.numColumns + 1];
        double xChunk = (this.corners.east - this.corners.west) / this.numColumns;
        double yChunk = (this.corners.north - this.corners.south) / this.numRows;
        for (int i = 0; i < censusData.length; i++) {
            int xOffset = 0;
            int yOffset = 0;
            double xCoord = (censusData[i].longitude - this.corners.west) / xChunk;
            double yCoord = (censusData[i].latitude - this.corners.south) / yChunk;
            if (xCoord % 1 == 0 && xCoord < this.numColumns) {
                xOffset = 1;
            }
            if (yCoord % 1 == 0 && yCoord < this.numRows) {
                yOffset = 1;
            }
            int xBlock = Math.min((int) Math.ceil(xCoord) + xOffset, numColumns);
            int yBlock = Math.min((int) Math.ceil(yCoord) + yOffset, numRows);
            arr[yBlock][xBlock] += censusData[i].population;
        }
        return arr;
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
