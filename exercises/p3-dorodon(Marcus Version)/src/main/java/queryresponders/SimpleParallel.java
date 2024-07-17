package queryresponders;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;
import paralleltasks.CornerFindingTask;
import paralleltasks.GetPopulationTask;

import java.util.concurrent.ForkJoinPool;

public class SimpleParallel extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    private final CensusGroup[] data;
    private final MapCorners usCorners;
    private final int xBound;
    private final int yBound;

    public SimpleParallel(CensusGroup[] censusData, int numColumns, int numRows) {
        CornerFindingResult res = POOL.invoke(new CornerFindingTask(censusData, 0, censusData.length));

        this.totalPopulation = res.getTotalPopulation();
        this.data = censusData;
        this.usCorners = res.getMapCorners();
        this.xBound = numColumns;
        this.yBound = numRows;
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

       return POOL.invoke(new GetPopulationTask(this.data, 0, this.data.length, this.xBound, this.yBound,
                west, south, east, north, this.usCorners));
    }
}
