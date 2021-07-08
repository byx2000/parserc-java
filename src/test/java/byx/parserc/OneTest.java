package byx.parserc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import static byx.parserc.Parsers.*;

public class OneTest {
    @Test
    public void test1() throws ParseException {
        ParseResult<Character, Character> r = eq('a').parse(new StringInputCursor("abc"));
        assertEquals('a', r.getResult());
        assertEquals("bc", r.getRemain().toString());
    }

    @Test
    public void test2() throws ParseException {
        ParseResult<Character, Character> r = eq('a').parse(new StringInputCursor("a"));
        assertEquals('a', r.getResult());
        assertEquals("", r.getRemain().toString());
    }

    @Test
    public void test3() {
         assertThrows(ParseException.class, () -> eq('a').parse(new StringInputCursor("")));
    }

    @Test
    public void test4() {
        assertThrows(ParseException.class, () -> eq('a').parse(new StringInputCursor("")));
    }

    @Test
    public void test5() {
        assertThrows(ParseException.class, () -> eq('a').parse(new StringInputCursor("xyz")));
    }
}
