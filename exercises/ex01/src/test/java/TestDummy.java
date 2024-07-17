import org.junit.Test;

import java.math.BigInteger;

public class TestDummy {

    @Test
    public void guesserRuns() {
        // Only tests that we are able to compile and run.
        // Click the green Play button, and choose "Run 'guesserRuns()'".
        // You should see a RuntimeException being thrown.
        Guesser.findNumber(new DummyChooser());
    }

    public static class DummyChooser implements Chooser {
        @Override
        public String guess(BigInteger i) {
            BigInteger valueToGuess = BigInteger.valueOf(69);
            int guessResult = valueToGuess.compareTo(i);
            if (guessResult == -1) {
                return "lower";
            } else if (guessResult == 1){
                return "higher";
            } else {
                return "correct";
            }
            //return "correct";
        }
    }
}
