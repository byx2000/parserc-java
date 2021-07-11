package byx.parserc;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static byx.parserc.Parsers.*;

public class RepeatTest {
    @Test
    public void test() throws ParseException {
        Parser<List<Character>, Character> p = repeat(one('a'), 3, 5);

        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("xyz")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("axyz")));
        assertThrows(ParseException.class, () -> p.parse(new StringInputCursor("aaxyz")));

        ParseResult<List<Character>, Character> r = p.parse(new StringInputCursor("aaaxyz"));
        assertEquals(Arrays.asList('a', 'a', 'a'), r.getResult());
        assertEquals("xyz", r.getRemain().toString());

        r = p.parse(new StringInputCursor("aaaa"));
        assertEquals(Arrays.asList('a', 'a', 'a', 'a'), r.getResult());
        assertEquals("", r.getRemain().toString());

        r = p.parse(new StringInputCursor("aaaaaxyz"));
        assertEquals(Arrays.asList('a', 'a', 'a', 'a', 'a'), r.getResult());
        assertEquals("xyz", r.getRemain().toString());

        r = p.parse(new StringInputCursor("aaaaa"));
        assertEquals(Arrays.asList('a', 'a', 'a', 'a', 'a'), r.getResult());
        assertEquals("", r.getRemain().toString());

        r = p.parse(new StringInputCursor("aaaaaaxyz"));
        assertEquals(Arrays.asList('a', 'a', 'a', 'a', 'a'), r.getResult());
        assertEquals("axyz", r.getRemain().toString());

        r = p.parse(new StringInputCursor("aaaaaa"));
        assertEquals(Arrays.asList('a', 'a', 'a', 'a', 'a'), r.getResult());
        assertEquals("a", r.getRemain().toString());
    }
}
