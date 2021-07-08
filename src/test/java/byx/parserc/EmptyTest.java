package byx.parserc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static byx.parserc.Parsers.*;

public class EmptyTest {
    @Test
    public void test() throws ParseException {
        Parser<Character, Character> p = empty();
        ParseResult<Character, Character> r = p.parse(new StringInputCursor(""));
        assertNull(r.getResult());
        assertEquals("", r.getRemain().toString());

        r = p.parse(new StringInputCursor("a"));
        assertNull(r.getResult());
        assertEquals("a", r.getRemain().toString());

        r = p.parse(new StringInputCursor("abc"));
        assertNull(r.getResult());
        assertEquals("abc", r.getRemain().toString());
    }
}
