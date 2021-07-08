package byx.parserc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static byx.parserc.Parsers.*;

public class NoneOfTest {
    @Test
    public void test1() throws ParseException {
        Parser<Character, Character> p = noneOf('a', 'b', 'c');
        ParseResult<Character, Character> r = p.parse(new StringInputCursor("xyz"));
        assertEquals('x', r.getResult());
        assertEquals("yz", r.getRemain().toString());

        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("abc")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("bxy")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("c")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("")));
    }

    @Test
    public void test2() throws ParseException {
        Parser<Character, Character> p = noneOf();
        ParseResult<Character, Character> r = p.parse(new StringInputCursor("abc"));
        assertEquals('a', r.getResult());
        assertEquals("bc", r.getRemain().toString());

        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("")));
    }
}
