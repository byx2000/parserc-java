package byx.parserc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CursorTest {
    @Test
    public void test() throws ParseException {
        Cursor c = new Cursor("hello", 3);
        assertEquals("hello", c.getInput());
        assertEquals(3, c.getIndex());
        assertFalse(c.end());
        assertEquals("lo", c.toString());
        assertEquals("hello\n   ^", c.toFriendlyString());

        c = c.next();
        assertEquals("hello", c.getInput());
        assertEquals(4, c.getIndex());
        assertFalse(c.end());

        c = c.next();
        assertEquals("hello", c.getInput());
        assertEquals(5, c.getIndex());
        assertTrue(c.end());
    }
}
