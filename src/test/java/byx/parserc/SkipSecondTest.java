package byx.parserc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static byx.parserc.Parsers.*;

public class SkipSecondTest {
    @Test
    public void test() throws ParseException {
        Parser<Character, Character> p1 = range('a', 'z');
        Parser<Integer, Character> p2 = range('0', '9').map(c -> c - '0');
        Parser<Character, Character> p = p1.skip(p2);

        ParseResult<Character, Character> r = p.parse(new StringInputCursor("a1"));
        assertEquals('a', r.getResult());
        assertEquals("", r.getRemain().toString());

        r = p.parse(new StringInputCursor("a5xyz"));
        assertEquals('a', r.getResult());
        assertEquals("xyz", r.getRemain().toString());
    }
}
