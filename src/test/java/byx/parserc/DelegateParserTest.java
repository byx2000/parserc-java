package byx.parserc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static byx.parserc.Parsers.*;

public class DelegateParserTest {
    @Test
    public void test1() throws ParseException {
        DelegateParser<Character, Character> p = new DelegateParser<>();
        ParseResult<Character, Character> r = p.parse(new StringInputCursor("abc"));
        assertNull(r.getResult());
        assertEquals("abc", r.getRemain().toString());
    }

    @Test
    public void test2() throws ParseException {
        DelegateParser<Character, Character> p = new DelegateParser<>();
        p.set(one('a'));
        ParseResult<Character, Character> r = p.parse(new StringInputCursor("abc"));
        assertEquals('a', r.getResult());
        assertEquals("bc", r.getRemain().toString());
    }
}
