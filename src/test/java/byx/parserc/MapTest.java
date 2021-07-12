package byx.parserc;

import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static byx.parserc.Parsers.*;

public class MapTest {
    @Test
    public void test1() throws ParseException {
        Parser<String, Character> p = one('a').oneOrMore()
                .map(chs -> chs.stream()
                        .map(Objects::toString)
                        .collect(Collectors.joining()));
        ParseResult<String, Character> r = p.parse(new StringInputCursor("aaaxyz"));
        assertEquals("aaa", r.getResult());
        assertEquals("xyz", r.getRemain().toString());
    }

    @Test
    public void test2() throws ParseException {
        Parser<Integer, Character> p = one('1').or(one('2')).or(one('3')).map(c -> c - '0');
        ParseResult<Integer, Character> r = p.parse(new StringInputCursor("2"));
        assertEquals(2, r.getResult());
        assertEquals("", r.getRemain().toString());
    }
}
