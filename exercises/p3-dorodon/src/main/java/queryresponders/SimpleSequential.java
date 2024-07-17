package queryresponders;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

public class SimpleSequential extends QueryResponder {
    private CensusGroup[] censusData;
    private int numColumns;
    private int numRows;
    private MapCorners corners;

    public SimpleSequential(CensusGroup[] censusData, int numColumns, int numRows) {
        this.censusData = censusData;
        this.numColumns = numColumns;
        this.numRows = numRows;
        this.corners = new MapCorners(censusData[0]);
        this.totalPopulation = censusData[0].population;
        for (int i = 1; i < censusData.length; i++) {
            this.corners = this.corners.encompass(new MapCorners(censusData[i]));
            this.totalPopulation += censusData[i].population;
        }
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        if (west < 1 || west > numColumns || south < 1 || south > numRows ||
        east < west || east > numColumns || north < south || north > numRows) {
            throw new IllegalArgumentException();
        }
        int population = 0;
        double width = this.corners.east - this.corners.west;
        double height = this.corners.north - this.corners.south;
        double xChunk = width / this.numColumns;
        double yChunk = height / this.numRows;
        double minLon = (west - 1) * xChunk + this.corners.west;
        double maxLon = east * xChunk + this.corners.west;
        double minLat = (south - 1) * yChunk + this.corners.south;
        double maxLat = north * yChunk + this.corners.south;
        for (CensusGroup block : censusData) {
            if (minLon <= block.longitude && block.longitude < maxLon &&
                    minLat <= block.latitude && block.latitude < maxLat ||
                    block.longitude == maxLon && maxLon == this.corners.east &&
                    minLat <= block.latitude && block.latitude < maxLat ||
                    block.latitude == maxLat && maxLat == this.corners.north &&
                    minLon <= block.longitude && block.longitude < maxLon) {
                population += block.population;
            }
        }
        return population;
    }
}
