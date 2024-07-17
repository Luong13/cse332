package queryresponders;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

public class SimpleSequential extends QueryResponder {
    private final CensusGroup[] data;
    private final int xBound;
    private final int yBound;
    private final MapCorners usCorners;

    public SimpleSequential(CensusGroup[] censusData, int numColumns, int numRows) {
        MapCorners usCorners = new MapCorners(censusData[0]);

        // Fold left over censusData to create MapCorners that encapsulates all points
        for(int i = 0; i < censusData.length; i++) {
            CensusGroup curr = censusData[i];
            this.totalPopulation += curr.population;
            usCorners = usCorners.encompass(new MapCorners(curr));
        }

        this.data = censusData;
        this.xBound = numColumns;
        this.yBound = numRows;
        this.usCorners = usCorners;
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        boolean invalidRect = west < 1 || west > this.xBound
                || south < 1 || south > this.yBound
                || east < west || east > this.xBound
                || north < south || north > this.yBound;

        if(invalidRect) {
            throw new IllegalArgumentException("Invalid rectangle");
        }

        double colSize = (this.usCorners.east - this.usCorners.west) / this.xBound; // Width of our columns
        double rowSize = (this.usCorners.north - this.usCorners.south) / this.yBound; // Height of our rows

        int total = 0;
        for(int i = 0; i < this.data.length; i++) {
            CensusGroup curr = this.data[i];
            int col = (int)((curr.longitude - usCorners.west) / colSize); // Column which curr falls into (0-based)
            int row = (int)((curr.latitude - usCorners.south) / rowSize); // Row which curr falls into (0-based)

            // true if curr falls into a space which is valid with respect to parameters, false otherwise
            boolean valid = (col >= west - 1 && col < east && row >= south - 1 && row < north)
                            || (col == east && east == xBound && row >= south - 1 && row < north)
                            || (row == north && north == yBound && col >= west - 1 && col < east)
                            || (row == north && north == yBound && col == east && east == xBound);

            if(valid) {
                total += curr.population;
            }
        }
        return total;
    }
}
