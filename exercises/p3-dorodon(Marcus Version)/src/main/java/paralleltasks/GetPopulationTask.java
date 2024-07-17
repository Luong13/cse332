package paralleltasks;

import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.concurrent.RecursiveTask;

/*
   1) This class is the parallel version of the getPopulation() method from version 1 for use in version 2
   2) SEQUENTIAL_CUTOFF refers to the maximum number of census groups that should be processed by a single parallel task
   3) The double parameters(w, s, e, n) represent the bounds of the query rectangle
   4) The compute method returns an Integer representing the total population contained in the query rectangle
 */
public class GetPopulationTask extends RecursiveTask<Integer> {
    final static int SEQUENTIAL_CUTOFF = 1000;
    CensusGroup[] data;
    int lo, hi;
    int xBound, yBound;
    double w, s, e, n;
    MapCorners grid;

    public GetPopulationTask(CensusGroup[] censusGroups, int lo, int hi, int xBound, int yBound,
                             double w, double s, double e, double n, MapCorners grid) {
        this.data = censusGroups;
        this.lo = lo;
        this.hi = hi;
        this.xBound = xBound;
        this.yBound = yBound;
        this.w = w;
        this.s = s;
        this.e = e;
        this.n = n;
        this.grid = grid;
    }

    // Returns a number for the total population
    @Override
    protected Integer compute() {
        if(this.hi - this.lo <= SEQUENTIAL_CUTOFF) {
            return sequentialGetPopulation();
        }
        GetPopulationTask left = new GetPopulationTask(this.data,
                this.lo, (this.lo + this.hi) / 2,
                this.xBound, this.yBound,
                this.w, this.s, this.e, this.n, this.grid);
        GetPopulationTask right = new GetPopulationTask(this.data,
                (this.lo + this.hi) / 2, this.hi,
                this.xBound, this.yBound,
                this.w, this.s, this.e, this.n, this.grid);
        left.fork();

        return right.compute() + left.join();
    }

    private Integer sequentialGetPopulation() {
        double colSize = (this.grid.east - this.grid.west) / this.xBound; // Width of our columns
        double rowSize = (this.grid.north - this.grid.south) / this.yBound; // Height of our rows

        int total = 0;
        for(int i = this.lo; i < this.hi; i++) {
            CensusGroup curr = this.data[i];
            int col = (int)((curr.longitude - this.grid.west) / colSize); // Column which curr falls into (0-based)
            int row = (int)((curr.latitude - this.grid.south) / rowSize); // Row which curr falls into (0-based)

            // true if curr falls into a space which is valid with respect to parameters, false otherwise
            boolean valid = (col >= this.w - 1 && col < this.e && row >= this.s - 1 && row < this.n)
                    || (col == this.e && this.e == xBound && row >= this.s - 1 && row < this.n)
                    || (row == this.n && this.n == yBound && col >= this.w - 1 && col < this.e)
                    || (row == this.n && this.n == yBound && col == this.e && this.e == xBound);

            if(valid) {
                total += curr.population;
            }
        }
        return total;
    }
}
