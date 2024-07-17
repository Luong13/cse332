import java.math.BigInteger;

public class Guesser {
    /**
     * This method must return the number Chooser c has chosen. c can guess() any
     * number and tell you whether the number is "correct", "higher", or "lower".
     *
     * @param c The "chooser" that has chosen a number you must guess.
     * @return The number that the "chooser" has chosen
     */
    public static BigInteger findNumber(Chooser c) {
        // Tip: If you're not sure how to work with BigInteger numbers, we encourage
        // you to look up its Javadoc online.
        BigInteger n = BigInteger.valueOf(2);
        while(c.guess(n).equalsIgnoreCase("higher")){
            n = n.multiply(BigInteger.valueOf(2));
        }
        BigInteger max = n;
        BigInteger min = n.divide(BigInteger.valueOf(2));
        while(!c.guess(n).equalsIgnoreCase("correct")){
            n = (max.add(min)).divide(BigInteger.valueOf(2));
            if (c.guess(n).equalsIgnoreCase("lower")){
                max = n;
            } else {
                min = n;
            }
        }
        return n;
        //throw new RuntimeException("Remove this line and implement me!");
    }
}
