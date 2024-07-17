package paralleltasks;

import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;
import java.util.concurrent.RecursiveTask;

/*
   1) This class will do the corner finding from version 1 in parallel for use in versions 2, 4, and 5
   2) SEQUENTIAL_CUTOFF refers to the maximum number of census groups that should be processed by a single parallel task
   3) The compute method returns a result of a MapCorners and an Integer.
        - The MapCorners will represent the extremes/bounds/corners of the entire land mass (latitude and longitude)
        - The Integer value should represent the total population contained inside the MapCorners
 */

public class CornerFindingTask extends RecursiveTask<CornerFindingResult> {
    final int SEQUENTIAL_CUTOFF = 10000;
    CensusGroup[] censusGroups;
    int lo, hi;

    public CornerFindingTask(CensusGroup[] censusGroups, int lo, int hi) {
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
    }

    // Returns a pair of MapCorners for the grid and Integer for the total population
    // Key = grid, Value = total population
    @Override
    protected CornerFindingResult compute() {
        if(this.hi - this.lo <= SEQUENTIAL_CUTOFF) {
            return this.sequentialCornerFinding(this.censusGroups, this.lo, this.hi);
        }
        CornerFindingTask left = new CornerFindingTask(this.censusGroups, this.lo, (this.lo + this.hi) / 2);
        CornerFindingTask right = new CornerFindingTask(this.censusGroups, (this.lo + this.hi) / 2, this.hi);
        left.fork();

        CornerFindingResult rightAns = right.compute();
        CornerFindingResult leftAns = left.join();

        return new CornerFindingResult(leftAns.getMapCorners().encompass(rightAns.getMapCorners()),
                          leftAns.getTotalPopulation() + rightAns.getTotalPopulation());
    }

    private CornerFindingResult sequentialCornerFinding(CensusGroup[] censusGroups, int lo, int hi) {
        int pop = 0;
        MapCorners corners = new MapCorners(censusGroups[lo]);
        pop += censusGroups[lo].population;

        for(int i = lo + 1; i < hi; i++) {
            pop += censusGroups[i].population;
            corners = corners.encompass(new MapCorners(censusGroups[i]));
        }
        return new CornerFindingResult(corners, pop);
    }
}

