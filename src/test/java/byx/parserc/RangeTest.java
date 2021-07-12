package byx.parserc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static byx.parserc.Parsers.*;

public class RangeTest {
    @Test
    public void test() throws ParseException {
        Parser<Character, Character> p = range('1', '3');

        ParseResult<Character, Character> r = p.parse(new StringInputCursor("1"));
        assertEquals('1', r.getResult());
        assertEquals("", r.getRemain().toString());

        r = p.parse(new StringInputCursor("2xyz"));
        assertEquals('2', r.getResult());
        assertEquals("xyz", r.getRemain().toString());

        r = p.parse(new StringInputCursor("3"));
        assertEquals('3', r.getResult());
        assertEquals("", r.getRemain().toString());

        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("0")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("4")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("a")));
    }
}
