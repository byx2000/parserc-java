package byx.parserc;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static byx.parserc.Parsers.*;

public class OneOrMoreTest {
    @Test
    public void test() throws ParseException {
        Parser<List<Character>, Character> p = oneOrMore(one('a'));

        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("x")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("xyz")));

        ParseResult<List<Character>, Character> r = p.parse(new StringInputCursor("a"));
        assertEquals(Collections.singletonList('a'), r.getResult());
        assertEquals("", r.getRemain().toString());

        r = p.parse(new StringInputCursor("axyz"));
        assertEquals(Collections.singletonList('a'), r.getResult());
        assertEquals("xyz", r.getRemain().toString());

        r = p.parse(new StringInputCursor("aaa"));
        assertEquals(Arrays.asList('a', 'a', 'a'), r.getResult());
        assertEquals("", r.getRemain().toString());

        r = p.parse(new StringInputCursor("aaaxyz"));
        assertEquals(Arrays.asList('a', 'a', 'a'), r.getResult());
        assertEquals("xyz", r.getRemain().toString());
    }
}
