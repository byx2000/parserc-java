package byx.parserc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PairTest {
    @Test
    public void test() {
        Pair<Integer, String> pair = new Pair<>(123, "hello");
        assertEquals(123, pair.getFirst());
        assertEquals("hello", pair.getSecond());
        assertEquals("(123, hello)", pair.toString());
    }
}
