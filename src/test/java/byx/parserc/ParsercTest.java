package byx.parserc;

import byx.parserc.exception.ParseException;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static byx.parserc.Parsers.*;
import static org.junit.jupiter.api.Assertions.*;

public class ParsercTest {
    @Test
    public void testSuccess() {
        assertEquals(123, success(123).parse("abc"));
        assertNull(success().parse("abc"));
    }

    @Test
    public void testFail() {
        ParseException e1 = assertThrows(ParseException.class, () -> Parsers.fail("byx").parse("abc"));
        assertTrue(e1.getMessage().contains("byx"));
        assertThrows(ParseException.class, () -> Parsers.fail().parse("abc"));
    }

    @Test
    public void testEnd() {
        assertNull(end().parse(""));
        assertThrows(ParseException.class, () -> end().parse("abc"));
    }

    @Test
    public void testCh1() {
        Parser<Character> p = ch(c -> c == 'a');
        assertEquals('a', p.parse("a"));
        assertThrows(ParseException.class, () -> p.parse("b"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testCh2() {
        Parser<Character> p = ch('a');
        assertEquals('a', p.parse("a"));
        assertThrows(ParseException.class, () -> p.parse("b"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testAny() {
        Parser<Character> p = any();
        assertEquals('a', p.parse("a"));
        assertEquals('b', p.parse("b"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testRange1() {
        Parser<Character> p = range('d', 'f');
        assertEquals('d', p.parse("d"));
        assertEquals('e', p.parse("e"));
        assertEquals('f', p.parse("f"));
        assertThrows(ParseException.class, () -> p.parse("c"));
        assertThrows(ParseException.class, () -> p.parse("g"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testRange2() {
        Parser<Character> p = range('f', 'd');
        assertEquals('d', p.parse("d"));
        assertEquals('e', p.parse("e"));
        assertEquals('f', p.parse("f"));
        assertThrows(ParseException.class, () -> p.parse("c"));
        assertThrows(ParseException.class, () -> p.parse("g"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testChs1() {
        Parser<Character> p = chs('f', 'o', 'h');
        assertEquals('f', p.parse("f"));
        assertEquals('o', p.parse("o"));
        assertEquals('h', p.parse("h"));
        assertThrows(ParseException.class, () -> p.parse("b"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testChs2() {
        Parser<Character> p = chs('f');
        assertEquals('f', p.parse("f"));
        assertThrows(ParseException.class, () -> p.parse("b"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testChs3() {
        Parser<Character> p = chs();
        assertThrows(ParseException.class, () -> p.parse("b"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testNot1() {
        Parser<Character> p = not('f', 'o', 'h');
        assertEquals('b', p.parse("b"));
        assertThrows(ParseException.class, () -> p.parse("f"));
        assertThrows(ParseException.class, () -> p.parse("o"));
        assertThrows(ParseException.class, () -> p.parse("h"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testNot2() {
        Parser<Character> p = not('f');
        assertEquals('b', p.parse("b"));
        assertThrows(ParseException.class, () -> p.parse("f"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testNot3() {
        Parser<Character> p = not();
        assertEquals('b', p.parse("b"));
        assertEquals('f', p.parse("f"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testStr() {
        Parser<String> p = str("byx");
        assertEquals("byx", p.parse("byx"));
        assertThrows(ParseException.class, () -> p.parse("by"));
        assertThrows(ParseException.class, () -> p.parse("bytb"));
        assertThrows(ParseException.class, () -> p.parse("mnt"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testStrs1() {
        Parser<String> p = strs("apple", "amend", "byx");
        assertEquals("apple", p.parse("apple"));
        assertEquals("amend", p.parse("amend"));
        assertEquals("byx", p.parse("byx"));
        assertThrows(ParseException.class, () -> p.parse("app"));
        assertThrows(ParseException.class, () -> p.parse("bycd"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testStrs2() {
        Parser<String> p = strs("byx");
        assertEquals("byx", p.parse("byx"));
        assertThrows(ParseException.class, () -> p.parse("by"));
        assertThrows(ParseException.class, () -> p.parse("bytb"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testStrs3() {
        Parser<String> p = strs();
        assertThrows(ParseException.class, () -> p.parse("abc"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testAnd1() {
        Parser<Pair<String, Character>> p = str("hello").and(ch('a'));
        assertEquals(new Pair<>("hello", 'a'), p.parse("helloa"));
        assertThrows(ParseException.class, () -> p.parse("hellob"));
        assertThrows(ParseException.class, () -> p.parse("byx"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testAnd2() {
        Parser<Pair<String, Character>> p = str("hello").and('a');
        assertEquals(new Pair<>("hello", 'a'), p.parse("helloa"));
        assertThrows(ParseException.class, () -> p.parse("hellob"));
        assertThrows(ParseException.class, () -> p.parse("byx"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testAnd3() {
        Parser<Pair<String, String>> p = str("hello").and("abc");
        assertEquals(new Pair<>("hello", "abc"), p.parse("helloabc"));
        assertThrows(ParseException.class, () -> p.parse("helloxyz"));
        assertThrows(ParseException.class, () -> p.parse("byx"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testSeq1() {
        Parser<List<Object>> p = seq(ch('a'), str("bcd"), ch('e'));
        assertEquals(List.of('a', "bcd", 'e'), p.parse("abcde"));
        assertThrows(ParseException.class, () -> p.parse("abcdk"));
        assertThrows(ParseException.class, () -> p.parse("amnpuk"));
        assertThrows(ParseException.class, () -> p.parse("byx"));
        assertThrows(ParseException.class, () -> p.parse("a"));
        assertThrows(ParseException.class, () -> p.parse("abc"));
        assertThrows(ParseException.class, () -> p.parse("abcd"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testSeq2() {
        Parser<List<Object>> p = seq(str("abc"));
        assertEquals(List.of("abc"), p.parse("abc"));
        assertThrows(ParseException.class, () -> p.parse("axy"));
        assertThrows(ParseException.class, () -> p.parse("ab"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testSeq3() {
        Parser<List<Object>> p = seq();
        assertEquals(Collections.emptyList(), p.parse("abc"));
        assertEquals(Collections.emptyList(), p.parse(""));
    }

    @Test
    public void testOr() {
        Parser<Character> p = ch('a').or(ch('b'));
        assertEquals('a', p.parse("a"));
        assertEquals('b', p.parse("b"));
        assertThrows(ParseException.class, () -> p.parse("x"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testOneOf1() {
        Parser<Character> p = oneOf(ch('a'), ch('b'), ch('c'));
        assertEquals('a', p.parse("a"));
        assertEquals('b', p.parse("b"));
        assertEquals('c', p.parse("c"));
        assertThrows(ParseException.class, () -> p.parse("d"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testOneOf2() {
        Parser<Character> p = oneOf(ch('a'));
        assertEquals('a', p.parse("a"));
        assertThrows(ParseException.class, () -> p.parse("d"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testOneOf3() {
        Parser<Object> p = oneOf();
        assertThrows(ParseException.class, () -> p.parse("a"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testOneOf4() {
        Parser<Object> p = oneOf(str("hello"), ch('a'), ch('b'));
        assertEquals("hello", p.parse("hello"));
        assertEquals('a', p.parse("a"));
        assertEquals('b', p.parse("b"));
        assertThrows(ParseException.class, () -> p.parse("xyz"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testMap() {
        Parser<Integer> p = str("hello").map(String::length);
        assertEquals(5, p.parse("hello"));
        assertThrows(ParseException.class, () -> p.parse("hi"));
    }

    @Test
    public void testMany() {
        Parser<List<Character>> p = ch('a').many();
        assertEquals(List.of('a'), p.parse("a"));
        assertEquals(List.of('a', 'a', 'a'), p.parse("aaa"));
        assertEquals(Collections.emptyList(), p.parse("bbb"));
        assertEquals(Collections.emptyList(), p.parse(""));
    }

    @Test
    public void testMany1() {
        Parser<List<Character>> p = ch('a').many1();
        assertEquals(List.of('a'), p.parse("a"));
        assertEquals(List.of('a', 'a', 'a'), p.parse("aaa"));
        assertThrows(ParseException.class, () -> p.parse("bbb"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testRepeat1() {
        Parser<List<Character>> p = ch('a').repeat(3, 5);
        assertEquals(List.of('a', 'a', 'a'), p.parse("aaa"));
        assertEquals(List.of('a', 'a', 'a', 'a'), p.parse("aaaa"));
        assertEquals(List.of('a', 'a', 'a', 'a', 'a'), p.parse("aaaaa"));
        assertEquals(List.of('a', 'a', 'a', 'a', 'a'), p.parse("aaaaaa"));
        assertEquals(new Pair<>(List.of('a', 'a', 'a', 'a', 'a'), 'a'), p.and('a').parse("aaaaaa"));
        assertThrows(ParseException.class, () -> p.parse("aa"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testRepeat2() {
        Parser<List<Character>> p = ch('a').repeat(3);
        assertEquals(List.of('a', 'a', 'a'), p.parse("aaa"));
        assertThrows(ParseException.class, () -> p.parse("a"));
        assertThrows(ParseException.class, () -> p.parse("aa"));
        assertThrows(ParseException.class, () -> p.parse("bbb"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testOpt() {
        Parser<Character> p = ch('a').opt('x');
        assertEquals('a', p.parse("a"));
        assertEquals('x', p.parse("b"));
        assertEquals('x', p.parse(""));
    }

    @Test
    public void testLazy() {
        int[] val = {0};
        Parser<Character> p = lazy(() -> {
            val[0] = 1;
            return ch('a');
        });
        assertEquals(0, val[0]);
        assertEquals('a', p.parse("a"));
        assertEquals(1, val[0]);
    }

    @Test
    public void testSurround1() {
        Parser<Character> p = ch('a').surround(ch('('), ch(')'));
        assertEquals('a', p.parse("(a)"));
        assertThrows(ParseException.class, () -> p.parse("(a"));
        assertThrows(ParseException.class, () -> p.parse("a)"));
        assertThrows(ParseException.class, () -> p.parse("a"));
        assertThrows(ParseException.class, () -> p.parse("(b)"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testSurround2() {
        Parser<Character> p = ch('a').surround(str("***"));
        assertEquals('a', p.parse("***a***"));
        assertThrows(ParseException.class, () -> p.parse("***a**"));
        assertThrows(ParseException.class, () -> p.parse("*a***"));
        assertThrows(ParseException.class, () -> p.parse("*a**"));
        assertThrows(ParseException.class, () -> p.parse("***b***"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testSkipFirst() {
        Parser<String> p = skip(ch('a')).and(str("bc"));
        assertEquals("bc", p.parse("abc"));
    }

    @Test
    public void testSkipSecond() {
        Parser<Character> p = ch('a').skip(str("bc"));
        assertEquals('a', p.parse("abc"));
    }

    @Test
    public void testThen() {
        Parser<Character> alpha = range('a', 'z').or(range('A', 'Z'));
        Parser<String> tagName = alpha.many1().map(this::join);
        Parser<String> tagContent = not('<').many1().map(this::join);
        Parser<Pair<String, String>> tag = skip(ch('<')).and(tagName).skip(ch('>'))
            .then(r -> tagContent.skip(str("</").and(str(r.getResult())).and(">")));

        assertEquals(new Pair<>("body", "content"), tag.parse("<body>content</body>"));
        assertThrows(ParseException.class, () -> tag.parse("<aaa>bbb</ccc>"));
    }

    private String join(List<?> list) {
        return list.stream().map(Objects::toString).collect(Collectors.joining(""));
    }

    @Test
    public void testFatal() {
        Parser<List<Object>> p1 = seq(
                ch('a'),
                ch('b').fatal(c -> new MyParseException("expected b"))
        );
        Parser<List<Object>> p2 = seq(
                ch('b'),
                ch('y').fatal(c -> new MyParseException("expected y")),
                ch('c').fatal(c -> new MyParseException("expected c"))
        );
        Parser<List<Object>> p3 = seq(
                ch('c'),
                ch('m').fatal(c -> new MyParseException("expected m"))
        );
        Parser<List<Object>> p = oneOf(p1, p2, p3)
                .fatal(c -> new MyParseException(c, "a or b or c"));
        MyParseException e1 = assertThrowsExactly(MyParseException.class, () -> p.parse("ax"));
        assertTrue(e1.getMessage().contains("expected b"));
        MyParseException e2 = assertThrowsExactly(MyParseException.class, () -> p.parse("byx"));
        assertTrue(e2.getMessage().contains("expected c"));
        MyParseException e3 = assertThrowsExactly(MyParseException.class, () -> p.parse("bxc"));
        assertTrue(e3.getMessage().contains("expected y"));
        MyParseException e4 = assertThrowsExactly(MyParseException.class, () -> p.parse("cv"));
        assertTrue(e4.getMessage().contains("expected m"));
        MyParseException e5 = assertThrowsExactly(MyParseException.class, () -> p.parse("dfagdf"));
        assertTrue(e5.getMessage().contains("a or b or c"));
        assertEquals(0, e5.getCursor().index());
    }

    @Test
    public void testExpect() {
        Parser<String> p = skip(expect(str("abc"))).and(str("abcde"));
        assertEquals("abcde", p.parse("abcde"));
        assertThrows(ParseException.class, () -> p.parse("xyz"));
    }

    @Test
    public void testNot() {
        Parser<String> p = skip(not(str("abc"))).and(str("abxyz"));
        assertEquals("abxyz", p.parse("abxyz"));
        assertThrows(ParseException.class, () -> p.parse("abcde"));
    }
}
