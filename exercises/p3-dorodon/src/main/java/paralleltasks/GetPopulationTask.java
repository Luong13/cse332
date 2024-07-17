package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/*
   1) This class is the parallel version of the getPopulation() method from version 1 for use in version 2
   2) SEQUENTIAL_CUTOFF refers to the maximum number of census groups that should be processed by a single parallel task
   3) The double parameters(w, s, e, n) represent the bounds of the query rectangle
   4) The compute method returns an Integer representing the total population contained in the query rectangle
 */
public class GetPopulationTask extends RecursiveTask<Integer> {
    //private static final ForkJoinPool POOL = new ForkJoinPool();
    final static int SEQUENTIAL_CUTOFF = 1000;
    CensusGroup[] censusGroups;
    int lo, hi;
    double w, s, e, n;
    MapCorners grid;

    public GetPopulationTask(CensusGroup[] censusGroups, int lo, int hi, double w, double s, double e, double n, MapCorners grid) {
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
        this.w = w;
        this.s = s;
        this.e = e;
        this.n = n;
        this.grid = grid;
    }

    // Returns a number for the total population
    @Override
    protected Integer compute() {
        if (hi - lo <= SEQUENTIAL_CUTOFF) {
            return sequentialGetPopulation(censusGroups, lo, hi, w, s, e, n);
        }

        int mid = lo + (hi - lo)/2;
        GetPopulationTask left = new GetPopulationTask(censusGroups, lo, mid, w, s, e, n, this.grid);
        GetPopulationTask right = new GetPopulationTask(censusGroups, mid, hi, w, s, e, n, this.grid);

        left.fork();
        int rightAns = right.compute();
        int leftAns = left.join();

        return rightAns + leftAns;
    }

    private Integer sequentialGetPopulation(CensusGroup[] censusGroups, int lo, int hi, double w, double s, double e, double n) {
        int population = 0;
        for (int i = lo; i < hi; i++) {
            CensusGroup block = censusGroups[i];
            if (w <= block.longitude && block.longitude < e &&
                    s <= block.latitude && block.latitude < n ||
                    block.longitude == e && e == this.grid.east &&
                            s <= block.latitude && block.latitude < n ||
                    block.latitude == n && n == this.grid.north &&
                            w <= block.longitude && block.longitude < e) {
                population += block.population;
            }
        }
        return population;
    }
}
