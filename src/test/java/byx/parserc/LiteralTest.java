package byx.parserc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static byx.parserc.Parsers.*;

public class LiteralTest {
    @Test
    public void test1() throws ParseException {
        Parser<String, Character> p = literal("abc", false);
        ParseResult<String, Character> r = p.parse(new StringInputCursor("abcde"));
        assertEquals("abc", r.getResult());
        assertEquals("de", r.getRemain().toString());

        r = p.parse(new StringInputCursor("Abcde"));
        assertEquals("Abc", r.getResult());
        assertEquals("de", r.getRemain().toString());

        r = p.parse(new StringInputCursor("ABCde"));
        assertEquals("ABC", r.getResult());
        assertEquals("de", r.getRemain().toString());

        r = p.parse(new StringInputCursor("abc"));
        assertEquals("abc", r.getResult());
        assertEquals("", r.getRemain().toString());

        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("abxyz")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("axyzw")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("xyzw")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("ab")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("a")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("")));
    }

    @Test
    public void test2() throws ParseException {
        Parser<String, Character> p = literal("abc", true);
        ParseResult<String, Character> r = p.parse(new StringInputCursor("abcde"));
        assertEquals("abc", r.getResult());
        assertEquals("de", r.getRemain().toString());

        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("Abcde")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("ABCde")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("abxyz")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("axyzw")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("xyzw")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("ab")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("a")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("")));
    }

    @Test
    public void test3() throws ParseException {
        Parser<String, Character> p = literal("abc");
        ParseResult<String, Character> r = p.parse(new StringInputCursor("abcde"));
        assertEquals("abc", r.getResult());
        assertEquals("de", r.getRemain().toString());

        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("Abcde")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("ABCde")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("abxyz")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("axyzw")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("xyzw")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("ab")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("a")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("")));
    }
}
