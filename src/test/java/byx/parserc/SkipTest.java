package byx.parserc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static byx.parserc.Parsers.*;

public class SkipTest {
    @Test
    public void test1() throws ParseException {
        Parser<Character, Character> p = skip(one('a')).concat(one('b')).skip(one('c'));

        ParseResult<Character, Character> r = p.parse(new StringInputCursor("abc"));
        assertEquals('b', r.getResult());
        assertEquals("", r.getRemain().toString());

        r = p.parse(new StringInputCursor("abcxyz"));
        assertEquals('b', r.getResult());
        assertEquals("xyz", r.getRemain().toString());
    }

    @Test
    public void test2() throws ParseException {
        Parser<Character, Character> p = one('a').skip(one('b')).skip(one('c'));

        ParseResult<Character, Character> r = p.parse(new StringInputCursor("abc"));
        assertEquals('a', r.getResult());
        assertEquals("", r.getRemain().toString());

        r = p.parse(new StringInputCursor("abcxyz"));
        assertEquals('a', r.getResult());
        assertEquals("xyz", r.getRemain().toString());
    }

    @Test
    public void test3() throws ParseException {
        Parser<Pair<Character, Character>, Character> p = one('a').skip(one('b')).concat(one('c')).skip(one('d'));

        ParseResult<Pair<Character, Character>, Character> r = p.parse(new StringInputCursor("abcd"));
        assertEquals('a', r.getResult().getFirst());
        assertEquals('c', r.getResult().getSecond());
        assertEquals("", r.getRemain().toString());

        r = p.parse(new StringInputCursor("abcdxyz"));
        assertEquals('a', r.getResult().getFirst());
        assertEquals('c', r.getResult().getSecond());
        assertEquals("xyz", r.getRemain().toString());
    }

    @Test
    public void test4() throws ParseException {
        Parser<Pair<Character, Character>, Character> p = skip(one('a')).concat(one('b')).skip(one('c')).concat(one('d'));

        ParseResult<Pair<Character, Character>, Character> r = p.parse(new StringInputCursor("abcd"));
        assertEquals('b', r.getResult().getFirst());
        assertEquals('d', r.getResult().getSecond());
        assertEquals("", r.getRemain().toString());

        r = p.parse(new StringInputCursor("abcdxyz"));
        assertEquals('b', r.getResult().getFirst());
        assertEquals('d', r.getResult().getSecond());
        assertEquals("xyz", r.getRemain().toString());
    }
}
