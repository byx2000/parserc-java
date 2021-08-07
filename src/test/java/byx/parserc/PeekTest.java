package byx.parserc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static byx.parserc.Parsers.*;

public class PeekTest {
    @Test
    public void test1() throws ParseException {
        Parser<Character, Character> p = peek(one('a'));
        ParseResult<Character, Character> r = p.parse(new StringInputCursor("abc"));
        assertEquals('a', r.getResult());
        assertEquals("abc", r.getRemain().toString());

        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("xyz")));
    }
}
