package duedate;

import cse332.types.AlphabeticString;
import datastructures.dictionaries.HashTrieMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class HashTrieMapTests {

    /**
     * Tests if insert, find, and findPrefix work in general.
     */
    @Test()
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void test_addFindFindPrefix_fewWords_correct() {
        HashTrieMap<Character, AlphabeticString, String> STUDENT_TRIE = new HashTrieMap<>(AlphabeticString.class);

        // Add all the words into the trie
        String[] words = {"dog", "doggy", "doge", "dragon", "cat", "draggin"};
        addAll(STUDENT_TRIE, words);

        // Makes sure the trie can:
        // find each word we inserted
        // findPrefix each word we inserted
        // NOT find garbage words
        // findPrefix every possible prefix of each word we inserted
        containsAllPaths(STUDENT_TRIE, words);

        // Makes sure the trie CANNOT find each invalid word
        String[] invalid = {"d", "cataract", "", "do"};
        doesNotContainAll(STUDENT_TRIE, invalid);
    }

    /**
     * Checks to see if basic delete functionality works.
     */
    @Test()
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void test_delete_fewWords_correct() {
        HashTrieMap<Character, AlphabeticString, String> STUDENT = new HashTrieMap<>(AlphabeticString.class);
        String[] words = {"dog", "doggy", "dreamer", "cat"};

        // Add all the words into the trie
        addAll(STUDENT, words);

        // Makes sure the trie can:
        // find each word we inserted
        // findPrefix each word we inserted
        // NOT find garbage words
        // findPrefix every possible prefix of each word we inserted
        containsAllPaths(STUDENT, words);

        // Delete something that doesn't exist (shouldn't do anything)
        STUDENT.delete(toAlphabeticString("I don't exist"));
        // Delete a word that exists
        STUDENT.delete(toAlphabeticString("dreamer"));
        // Check if the other words still exist (it should)
        containsAllPaths(STUDENT, "dog", "doggy", "cat");
        // Check if the prefixes of "dreamer" exist (it should NOT)
        doesNotContainAllPrefixes(STUDENT, "dreamer", "dreame", "dream", "drea", "dre", "dr");
        // except "d" since it shares with "dog" and "doggy"
        assertTrue(STUDENT.findPrefix(toAlphabeticString("d")),
                   "Could not findPrefix d");

        // Delete a word that exists
        STUDENT.delete(toAlphabeticString("dog"));
        // Check if the other words still exist (it should)
        containsAllPaths(STUDENT, "doggy", "cat");

        // Delete a word that exists
        STUDENT.delete(toAlphabeticString("doggy"));
        // Check if the other words still exist (it should)
        containsAllPaths(STUDENT, "cat");
    }

    // UTILITY METHODS

    /**
     * Converts a String into an AlphabeticString
     */
    private static AlphabeticString toAlphabeticString(String s) {
        return new AlphabeticString(s);
    }

    /**
     * Checks if the trie contains the word and the expected value, and that all prefixes of
     * the word exist in the trie.
     *
     * Assumes that the expected value of the key word is word.toUpperCase().
     */
    private static void containsPath(HashTrieMap<Character, AlphabeticString, String> trie, String word) {
        AlphabeticString key = toAlphabeticString(word);

        // null if this trie contains no mapping for the word
        assertEquals(word.toUpperCase(), trie.find(key),
                     "Could not find " + key);
        // findPrefix should return true on the key itself
        assertTrue(trie.findPrefix(key),
                   "Could not findPrefix " + key);
        // Should not be able to find garbage words
        assertNull(trie.find(toAlphabeticString(word + "$")),
                   "Somehow found " + word + "$ even though it should not exist in the trie");

        // Should be able to find every prefix
        allPrefixesExist(trie, word);
    }

    /**
     * Returns true if all prefixes of a word exist in the trie.
     *
     * That is, if we do `trie.insert(new AlphabeticString("dog"), "some-value")`, this method
     * would check to see if "dog", "do", "d", and "" are all prefixes of the trie.
     */
    private static void allPrefixesExist(HashTrieMap<Character, AlphabeticString, String> trie, String word) {
        String accumulatedWord = "";
        for (char c : word.toCharArray()) {
            accumulatedWord += c;
            // We should be able to find every prefix of the word
            assertTrue(trie.findPrefix(toAlphabeticString(accumulatedWord)),
                       "Could not find prefix " + accumulatedWord + " of the word " + word);
        }
    }

    /***
     * Checks if the trie can:
     * find each word in words
     * findPrefix each word in words
     * NOT find garbage words
     * findPrefix every possible prefix of each word in words
     */
    private static void containsAllPaths(HashTrieMap<Character, AlphabeticString, String> trie, String... words) {
        for (String word : words) {
            containsPath(trie, word);
        }
    }

    /**
     * Checks if the trie was able to find every word in words
     */
    private static void doesNotContainAll(HashTrieMap<Character, AlphabeticString, String> trie, String... words) {
        for (String word : words) {
            assertNull(trie.find(toAlphabeticString(word)),
                       "Was able to find " + word + " even though its not meant to be there");
        }
    }

    /**
     * Checks if the trie canNOT findPrefix all the prefixes in prefixes
     */
    private static void doesNotContainAllPrefixes(HashTrieMap<Character, AlphabeticString, String> trie, String... prefixes) {
        for (String prefix : prefixes) {
            assertFalse(trie.findPrefix(toAlphabeticString(prefix)),
                        "Was able to findPrefix " + prefix + " even though its not meant to be there");
        }
    }

    /**
     * Adds all the words in words to the trie
     */
    private static void addAll(HashTrieMap<Character, AlphabeticString, String> trie, String... words) {
        for (String word : words) {
            trie.insert(toAlphabeticString(word), word.toUpperCase());
        }
    }
}
