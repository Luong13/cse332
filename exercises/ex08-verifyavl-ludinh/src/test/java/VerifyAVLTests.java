import verifyavl.AVLNode;
import verifyavl.VerifyAVL;
import org.json.simple.*;

import java.io.FileReader;
import java.io.IOException;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import static org.junit.Assert.*;


public class VerifyAVLTests {

    /***
     * Run this to test every tree in the json
     */
    @Test
    public void verifyAVLTest() throws IOException, ParseException {
        // Parse the tests.json file as a json object
        Object data = new JSONParser().parse(new FileReader("tests.json"));
        // Cast it to a JSONArray
        JSONArray jsonArray = (JSONArray) data;
        // For each element in the JSON array,
        // i.e. For each test case,
        for (Object o : jsonArray) {
            // Cast it to a JSONObject
            JSONObject test = (JSONObject) o;
            // Get the actual answer from
            boolean expected = (boolean) test.get("answer");
            JSONArray tree = (JSONArray) test.get("tree");
            AVLNode root = makeAvl(tree);
            boolean actual = VerifyAVL.verifyAVL(root);
            assertEquals("Failed: " + tree + " is a " + (expected ? "correct" : "wrong") + " tree but returned " + (actual ? "correct" : "wrong"), expected, actual);
        }
    }

    public static AVLNode makeAvl(JSONArray data) {
        if (data == null) {
            return null;
        }
        AVLNode left = makeAvl((JSONArray) data.get(2));
        AVLNode right = makeAvl((JSONArray) data.get(3));
        int key = (int) (long) data.get(0);
        int height = (int) (long) data.get(1);
        return new AVLNode(key, height, left, right);
    }
}
