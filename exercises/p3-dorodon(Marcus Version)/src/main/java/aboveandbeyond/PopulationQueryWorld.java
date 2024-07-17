package aboveandbeyond;


import cse332.interfaces.QueryResponder;
import cse332.types.CensusData;
import cse332.types.CensusGroup;
import queryresponders.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

/*
  Note: The worldcities.csv dataset (from https://simplemaps.com/data/world-cities) has data
  which covers the entire world's urban population. This is above 4 billion in total, so it exceeds
  the maximum 32-bit integer value. Therefore, although this parser works properly and you can execute queries
  on this dataset using the QueryResponders, you will face integer overflow because QueryResponder uses
  ints for totalPopulation and getPopulation. I am not going to change my implementations for my QueryResponders
  to suit this EC, but if you want to test it to ensure it works, just make QueryResponder.totalPopulation a double,
  QueryResponder.getPopulation() return a double, and change the getPopulation methods to return doubles
  and use doubles for local variables that track population instead of ints.
  Also, the worldcities.csv is not the original dataset from the link above. I needed to modify it to make
  parsing easier and to remove inconsistencies.
 */
public class PopulationQueryWorld {
    public static final int TOKENS_PER_LINE = 11;
    public static final int POPULATION_INDEX = 9;
    public static final int LATITUDE_INDEX = 2;
    public static final int LONGITUDE_INDEX = 3;

    public static CensusGroup[] parse(String filename) {
        CensusData result = new CensusData();
        String dataFolderPath = "data/";
        try {
            BufferedReader fileIn = new BufferedReader(new FileReader(dataFolderPath + filename));

            String oneLine = fileIn.readLine();
            while ((oneLine = fileIn.readLine()) != null) {
                String[] tokens = oneLine.split(",");
                if (tokens.length != TOKENS_PER_LINE)
                    throw new NumberFormatException();
                // Some populations have decimal places for whatever reason in this dataset
                int population = (int) Double.parseDouble(tokens[POPULATION_INDEX]);
                if (population != 0)
                    result.add(population,
                            Double.parseDouble(tokens[LATITUDE_INDEX]),
                            Double.parseDouble(tokens[LONGITUDE_INDEX]));
            }
            fileIn.close();
        } catch (IOException ioe) {
            System.err.println("Error opening/reading/writing input or output file.");
            System.exit(1);
        } catch (NumberFormatException nfe) {
            System.err.println(nfe.toString());
            System.err.println("Error in file format");
            System.exit(1);
        }
        return Arrays.stream(result.data)
                .limit(result.data_size)
                .toArray(CensusGroup[]::new);
    }

    /**
     * Argument 1: The file containing the input data
     * Argument 2: x, the number of columns in the grid for queries
     * Argument 3: y, the number of rows in the grid for queries
     * Argument 4: The version, -v1, -v2, -v3, -v4, or -v5
     **/
    public static void main(String[] args) {
        // Check if we have exactly 4 arguments:
        if (args.length != 4) {
            System.err.println(
                    "Not enough or too many arguments.\n" +
                            "Usage:\n" +
                            "Argument 1: The file containing the input data\n" +
                            "Argument 2: the number of columns in the grid for queries\n" +
                            "Argument 3: the number of rows in the grid for queries\n" +
                            "Argument 4: The version, -v1, -v2, -v3, -v4, or -v5"
            );
            System.exit(1);
        }

        // Parse arguments
        String fileName = args[0];
        int numColumns = parseDim(args[1], "x");
        int numRows = parseDim(args[2], "y");

        // Parse census data based on the given file
        CensusGroup[] censusData = parse(fileName);

        // Use the third argument to execute the correct version
        QueryResponder version = null;
        switch (args[3]) {
            case "-v1":
                version = new SimpleSequential(censusData, numColumns, numRows);
                break;
            case "-v2":
                version = new SimpleParallel(censusData, numColumns, numRows);
                break;
            case "-v3":
                version = new ComplexSequential(censusData, numColumns, numRows);
                break;
            case "-v4":
                version = new ComplexParallel(censusData, numColumns, numRows);
                break;
            case "-v5":
                version = new ComplexLockBased(censusData, numColumns, numRows);
                break;
            default:
                System.err.println("Invalid version");
                System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);
        // Keep asking for input until program sees any input that is not 4
        // integers on a line
        while (true) {
            System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
            // Assumes there are 4 ints separated by whitespace
            int west, south, east, north;
            try {
                west = scanner.nextInt();
                south = scanner.nextInt();
                east = scanner.nextInt();
                north = scanner.nextInt();
            } catch (Exception ignored) {  // Otherwise, exits
                break;
            }
            double rectanglePopulation = version.getPopulation(west, south, east, north);
            System.out.println("population of the U.S.: " + version.getTotalPopulation());
            float percentPopulation = 100 * (float) rectanglePopulation / (float) version.getTotalPopulation();
            System.out.println("population of rectangle: " + rectanglePopulation);
            System.out.printf("percent of total population: %.2f\n", percentPopulation);
        }

    }

    /**
     * Parses a String with the number of columns or rows into an int
     *
     * @param numDim       The number of columns or rows as a String
     * @param errorMessage The error message to output in case of failed parse
     * @return An int which is the number of columns or rows
     **/
    private static int parseDim(String numDim, String errorMessage) {
        try {
            return Integer.parseInt(numDim);
        } catch (NumberFormatException e) {
            System.err.println("Invalid " + errorMessage + " value");
            System.exit(1);
            return 0;
        }
    }
}

