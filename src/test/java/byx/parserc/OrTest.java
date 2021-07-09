package byx.parserc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static byx.parserc.Parsers.*;

public class OrTest {
    @Test
    public void test() throws ParseException {
        Parser<Character, Character> p = one('a').or(one('b'));
        ParseResult<Character, Character> r = p.parse(new StringInputCursor("abc"));
        assertEquals('a', r.getResult());
        assertEquals("bc", r.getRemain().toString());

        r = p.parse(new StringInputCursor("bac"));
        assertEquals('b', r.getResult());
        assertEquals("ac", r.getRemain().toString());

        r = p.parse(new StringInputCursor("a"));
        assertEquals('a', r.getResult());
        assertEquals("", r.getRemain().toString());

        r = p.parse(new StringInputCursor("b"));
        assertEquals('b', r.getResult());
        assertEquals("", r.getRemain().toString());

        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("xyz")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("x")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("")));
    }
}
