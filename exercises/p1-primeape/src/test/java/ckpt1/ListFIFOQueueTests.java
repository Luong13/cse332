package ckpt1;

import cse332.interfaces.worklists.WorkList;
import datastructures.worklists.ListFIFOQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListFIFOQueueTests {

    @Test()
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void test_addPeekNext_manyNumbers_correctStructure() {
        WorkList<Integer> STUDENT_QUEUE = new ListFIFOQueue<>();

        // Add numbers 0 - 999 (inclusive) to the queue
        for (int i = 0; i < 1000; i++) {
            // Add the number
            STUDENT_QUEUE.add(i);
            // Checks if the front of the queue is 0
            assertEquals(0, STUDENT_QUEUE.peek());
            // Checks if the queue is not empty
            assertTrue(STUDENT_QUEUE.hasWork());
            // Checks if the size is correct
            assertEquals((i + 1), STUDENT_QUEUE.size());
        }

        // Empty out the queue
        for (int i = 0; i < 999; i++) {
            // Checks if the queue is not empty
            assertTrue(STUDENT_QUEUE.hasWork());
            // Checks if the top of the queue is the correct number
            assertEquals(i, STUDENT_QUEUE.peek());
            // Removing the top of the queue should be the correct number
            assertEquals(i, STUDENT_QUEUE.next());
            // Checks if the size is correct
            assertEquals(999 - i, STUDENT_QUEUE.size());
        }
    }
}
