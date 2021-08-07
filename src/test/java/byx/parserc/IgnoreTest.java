package byx.parserc;

import org.junit.jupiter.api.Test;

import static byx.parserc.Parsers.*;
import static org.junit.jupiter.api.Assertions.*;

public class IgnoreTest {
    @Test
    public void test1() throws ParseException {
        Parser<Integer, Character> p = one('a').ignore(100);
        ParseResult<Integer, Character> r = p.parse(new StringInputCursor("abc"));
        assertEquals(100, r.getResult());
        assertEquals("bc", r.getRemain().toString());
    }

    @Test
    public void test2() throws ParseException {
        Parser<Pair<Integer, Character>, Character> p = one('a').ignore(100).concat(one('b'));
        ParseResult<Pair<Integer, Character>, Character> r = p.parse(new StringInputCursor("abc"));
        assertEquals(100, r.getResult().getFirst());
        assertEquals('b', r.getResult().getSecond());
        assertEquals("c", r.getRemain().toString());
    }

    @Test
    public void test3() throws ParseException {
        Parser<String, Character> p = one('a').ignore();
        ParseResult<String, Character> r = p.parse(new StringInputCursor("abc"));
        assertNull(r.getResult());
        assertEquals("bc", r.getRemain().toString());
    }

    @Test
    public void test4() throws ParseException {
        Parser<String, Character> p1 = one('a').ignore();
        Parser<Pair<String, Character>, Character> p = p1.concat(one('b'));
        ParseResult<Pair<String, Character>, Character> r = p.parse(new StringInputCursor("abc"));
        assertNull(r.getResult().getFirst());
        assertEquals('b', r.getResult().getSecond());
        assertEquals("c", r.getRemain().toString());
    }
}
