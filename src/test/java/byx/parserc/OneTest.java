package byx.parserc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import static byx.parserc.Parsers.*;

public class OneTest {
    @Test
    public void test() throws ParseException {
        Parser<Character, Character> p = one('a');
        ParseResult<Character, Character> r = p.parse(new StringInputCursor("abc"));
        assertEquals('a', r.getResult());
        assertEquals("bc", r.getRemain().toString());

        r = p.parse(new StringInputCursor("a"));
        assertEquals('a', r.getResult());
        assertEquals("", r.getRemain().toString());

        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("xyz")));
    }
}
