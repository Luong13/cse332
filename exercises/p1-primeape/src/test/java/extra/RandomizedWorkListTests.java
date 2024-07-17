package extra;
import datastructures.worklists.RandomizedWorkList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

public class RandomizedWorkListTests {

    /**
     * Tests if the RandomizedWorkList produces a uniform distribution when fewer or equal than capacity
     * elements are added.
     */
    @Test()
    @Timeout(value = 30000, unit = TimeUnit.MILLISECONDS)
    public void test_uniform_distribution_under_eq() {
        int samples = 70000;
        int n = 3;
        for (int x = 0 ; x < 2; x++) {
            HashMap<String, Integer> patterns = new HashMap<>();
            patterns.put("012", 0);
            patterns.put("021", 0);
            patterns.put("102", 0);
            patterns.put("120", 0);
            patterns.put("201", 0);
            patterns.put("210", 0);
            for (int i = 0; i < samples; i++) {
                RandomizedWorkList<String> list = new RandomizedWorkList<>(x + n);
                for (int j = 0; j < n; j++) {
                    list.add(String.valueOf(j));
                }
                StringBuilder strb = new StringBuilder();
                for (int j = 0; j < n; j++) {
                    strb.append(list.next());
                }
                String str = strb.toString();
                patterns.put(str, patterns.get(str) + 1);
            }
            for (Integer val : patterns.values()) {
                assert (val > 10000);
            }
        }
    }

    /**
     * Tests if the RandomizedWorkList produces a uniform distribution when more than capacity
     * elements are added.
     */
    @Test()
    @Timeout(value = 30000, unit = TimeUnit.MILLISECONDS)
    public void test_uniform_distribution_over() {
        int samples = 70000;
        int n = 2;
        HashMap<String, Integer> patterns = new HashMap<>();
        patterns.put("01", 0);
        patterns.put("10", 0);
        patterns.put("11", 0);
        patterns.put("00", 0);
        for (int i = 0; i < samples; i++) {
            RandomizedWorkList<String> list = new RandomizedWorkList<>(n);
            for (int j = 0; j < n; j++) {
                list.add(String.valueOf(j));
            }
            for (int j = 0; j < n; j++) {
                list.add(String.valueOf(j));
            }
            StringBuilder strb = new StringBuilder();
            for (int j = 0; j < n; j++) {
                strb.append(list.next());
            }
            String str = strb.toString();
            patterns.put(str, patterns.get(str) + 1);
        }
        for (Integer val : patterns.values()) {
            assert (val > 10000);
        }
    }

    /**
     * Tests that RandomizedWorkList blocks access after calling next,
     * but not after calling peek.
     */
    @Test()
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void test_blocking() {
        RandomizedWorkList<String> list = new RandomizedWorkList<>(10);
        for (int j = 0; j < 3; j++) {
            list.add(String.valueOf(j));
        }
        list.peek();
        list.add(String.valueOf(3));
        list.next();
        assertThrows(IllegalStateException.class, () -> list.add(String.valueOf(4)));
    }
}
