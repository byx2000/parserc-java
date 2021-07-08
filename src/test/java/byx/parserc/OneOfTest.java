package byx.parserc;

import org.junit.jupiter.api.Test;

import static byx.parserc.Parsers.*;
import static org.junit.jupiter.api.Assertions.*;

public class OneOfTest {
    @Test
    public void test1() throws ParseException {
        Parser<Character, Character> p = oneOf('a', 'b', 'c');
        ParseResult<Character, Character> r = p.parse(new StringInputCursor("abc"));
        assertEquals('a', r.getResult());
        assertEquals("bc", r.getRemain().toString());

        r = p.parse(new StringInputCursor("c"));
        assertEquals('c', r.getResult());
        assertEquals("", r.getRemain().toString());

        r = p.parse(new StringInputCursor("bxy"));
        assertEquals('b', r.getResult());
        assertEquals("xy", r.getRemain().toString());

        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("xyz")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("")));
    }

    @Test
    public void test2() {
        Parser<Character, Character> p = oneOf();
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("abc")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("a")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("")));
    }
}
