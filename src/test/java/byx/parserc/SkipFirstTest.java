package byx.parserc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static byx.parserc.Parsers.*;

public class SkipFirstTest {
    @Test
    public void test() throws ParseException {
        Parser<Character, Character> p1 = range('a', 'z');
        Parser<Integer, Character> p2 = range('0', '9').map(c -> c - '0');
        Parser<Integer, Character> p = skip(p1).concat(p2);

        ParseResult<Integer, Character> r = p.parse(new StringInputCursor("a1"));
        assertEquals(1, r.getResult());
        assertEquals("", r.getRemain().toString());

        r = p.parse(new StringInputCursor("b5xyz"));
        assertEquals(5, r.getResult());
        assertEquals("xyz", r.getRemain().toString());

    }
}
