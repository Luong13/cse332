package queryresponders;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;
import paralleltasks.CornerFindingTask;
import paralleltasks.GetPopulationTask;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class SimpleParallel extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    private CornerFindingTask cornerTask;
    private CornerFindingResult cornerResults;
    private CensusGroup[] censusData;
    private int numColumns;
    private int numRows;
    private MapCorners corners;

    public SimpleParallel(CensusGroup[] censusData, int numColumns, int numRows) {
        this.censusData = censusData;
        this.numColumns = numColumns;
        this.numRows = numRows;
        cornerTask = new CornerFindingTask(censusData, 0, censusData.length);
        cornerTask.fork();
        cornerResults = cornerTask.join();
        this.corners = cornerResults.getMapCorners();
        this.totalPopulation = cornerResults.getTotalPopulation();
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        if (west < 1 || west > numColumns || south < 1 || south > numRows ||
                east < west || east > numColumns || north < south || north > numRows) {
            throw new IllegalArgumentException();
        }

        double width = this.corners.east - this.corners.west;
        double height = this.corners.north - this.corners.south;
        double xChunk = width / this.numColumns;
        double yChunk = height / this.numRows;
        double minLon = (west - 1) * xChunk + this.corners.west;
        double maxLon = east * xChunk + this.corners.west;
        double minLat = (south - 1) * yChunk + this.corners.south;
        double maxLat = north * yChunk + this.corners.south;

        GetPopulationTask populationTask = new GetPopulationTask(censusData, 0, censusData.length,
                minLon, minLat, maxLon, maxLat, this.corners);
        populationTask.fork();
        return populationTask.join();
    }
}
