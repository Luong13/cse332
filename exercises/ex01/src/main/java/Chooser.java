import java.math.BigInteger;

/**
 * A Chooser will pre-emptively choose a number that a Guesser must guess.
 */
public interface Chooser {
    /**
     * You can use this method to guess any number and the chooser will return
     * a String on whether the number numToGuess is "correct", "higher", or "lower"
     *
     * @param numToGuess An arbitrary number to guess.
     * @return "correct" (your answer is correct),
     * "higher" (the correct answer is higher),
     * or "lower" (the correct answer is lower)
     */
    String guess(BigInteger numToGuess);
}
