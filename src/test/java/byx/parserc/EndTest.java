package byx.parserc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static byx.parserc.Parsers.*;

public class EndTest {
    @Test
    public void test1() throws ParseException {
        Parser<Character, Character> p = end();
        ParseResult<Character, Character> r = p.parse(new StringInputCursor(""));
        assertNull(r.getResult());
        assertEquals("", r.getRemain().toString());

        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("a")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("abc")));
    }

    @Test
    public void test2() throws ParseException {
        Parser<Character, Character> p = one('a').end();
        ParseResult<Character, Character> r = p.parse(new StringInputCursor("a"));
        assertEquals('a', r.getResult());
        assertEquals("", r.getRemain().toString());

        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("b")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("abc")));
    }
}
