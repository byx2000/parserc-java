package byx.parserc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static byx.parserc.Parsers.*;

public class ConcatTest {
    @Test
    public void test() throws ParseException {
        Parser<Pair<Character, Character>, Character> p = one('a').concat(one('b'));
        ParseResult<Pair<Character, Character>, Character> r = p.parse(new StringInputCursor("abc"));
        assertEquals('a', r.getResult().getFirst());
        assertEquals('b', r.getResult().getSecond());
        assertEquals("c", r.getRemain().toString());

        r = p.parse(new StringInputCursor("ab"));
        assertEquals('a', r.getResult().getFirst());
        assertEquals('b', r.getResult().getSecond());
        assertEquals("", r.getRemain().toString());

        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("acb")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("bac")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("xyz")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("a")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("b")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("")));
    }
}
