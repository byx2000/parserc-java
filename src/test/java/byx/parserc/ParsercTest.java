package byx.parserc;

import byx.parserc.exception.ParseException;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static byx.parserc.Parsers.fail;
import static byx.parserc.Parsers.*;
import static org.junit.jupiter.api.Assertions.*;

public class ParsercTest {
    private <R> void verifyParseResult(ParseResult<R> r, R expectedResult, int expectedBeforeIndex, int expectedRemainIndex) {
        assertEquals(expectedResult, r.getResult());
        assertEquals(expectedBeforeIndex, r.getBefore().index());
        assertEquals(expectedRemainIndex, r.getRemain().index());
    }

    @Test
    public void testSuccess1() {
        Parser<String> p = success(input -> {
            if (!input.end() && input.current() == 'a') {
                return "aaa";
            } else {
                return "bbb";
            }
        });

        verifyParseResult(p.parse(new Cursor("a")), "aaa", 0, 0);
        verifyParseResult(p.parse(new Cursor("b")), "bbb", 0, 0);
        verifyParseResult(p.parse(new Cursor("")), "bbb", 0, 0);
    }

    @Test
    public void testSuccess2() {
        Parser<String> p = success(() -> "byx");
        verifyParseResult(p.parse(new Cursor("abc")), "byx", 0, 0);
        verifyParseResult(p.parse(new Cursor("")), "byx", 0, 0);
    }

    @Test
    public void testSuccess3() {
        Parser<Integer> p = success(123);
        verifyParseResult(p.parse(new Cursor("abc")), 123, 0, 0);
        verifyParseResult(p.parse(new Cursor("")), 123, 0, 0);
    }

    @Test
    public void testSuccess4() {
        Parser<Integer> p = success();
        verifyParseResult(p.parse(new Cursor("abc")), null, 0, 0);
        verifyParseResult(p.parse(new Cursor("")), null, 0, 0);
    }

    @Test
    public void testEmpty1() {
        Parser<Integer> p = empty(123);
        verifyParseResult(p.parse(new Cursor("abc")), 123, 0, 0);
        verifyParseResult(p.parse(new Cursor("")), 123, 0, 0);
    }

    @Test
    public void testEmpty2() {
        Parser<Integer> p = empty();
        verifyParseResult(p.parse(new Cursor("abc")), null, 0, 0);
        verifyParseResult(p.parse(new Cursor("")), null, 0, 0);
    }

    @Test
    public void testFail1() {
        Parser<?> p = fail(cursor -> {
            if (!cursor.end() && cursor.current() == 'a') {
                return new MyParseException(cursor, "byx");
            } else {
                return new ParseException(cursor, "byx");
            }
        });

        assertThrows(MyParseException.class, () -> p.parse("abc"));
        assertThrows(ParseException.class, () -> p.parse("bbc"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testFail2() {
        Parser<?> p = fail(() -> new MyParseException(new Cursor("aaa"), "byx"));
        assertThrows(MyParseException.class, () -> p.parse("abc"));
        assertThrows(MyParseException.class, () -> p.parse(""));
    }

    @Test
    public void testFail3() {
        Parser<?> p = Parsers.fail("byx");
        ParseException e1 = assertThrowsExactly(ParseException.class, () -> p.parse("abc"));
        assertTrue(e1.getMessage().contains("byx"));
        ParseException e2 = assertThrowsExactly(ParseException.class, () -> p.parse(""));
        assertTrue(e2.getMessage().contains("byx"));
    }

    @Test
    public void testFail4() {
        Parser<?> p = Parsers.fail();
        assertThrowsExactly(ParseException.class, () -> p.parse("abc"));
        assertThrowsExactly(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testEnd1() {
        Parser<String> p = end("byx");
        verifyParseResult(p.parse(new Cursor("")), "byx", 0, 0);
        assertThrows(ParseException.class, () -> p.parse(new Cursor("abc")));
    }

    @Test
    public void testEnd2() {
        Parser<?> p = end();
        verifyParseResult(p.parse(new Cursor("")), null, 0, 0);
        assertThrows(ParseException.class, () -> p.parse(new Cursor("abc")));
    }

    @Test
    public void testCh1() {
        Parser<Character> p = ch(c -> c == 'a');
        verifyParseResult(p.parse(new Cursor("abc")), 'a', 0, 1);
        assertThrows(ParseException.class, () -> p.parse(new Cursor("def")));
        assertThrows(ParseException.class, () -> p.parse(new Cursor("")));
    }

    @Test
    public void testCh2() {
        Parser<Character> p = ch('a');
        verifyParseResult(p.parse(new Cursor("abc")), 'a', 0, 1);
        assertThrows(ParseException.class, () -> p.parse(new Cursor("def")));
        assertThrows(ParseException.class, () -> p.parse(new Cursor("")));
    }

    @Test
    public void testAny() {
        Parser<Character> p = any();
        verifyParseResult(p.parse(new Cursor("abc")), 'a', 0, 1);
        verifyParseResult(p.parse(new Cursor("bcd")), 'b', 0, 1);
        assertThrows(ParseException.class, () -> p.parse(new Cursor("")));
    }

    @Test
    public void testRange1() {
        Parser<Character> p = range('d', 'f');
        verifyParseResult(p.parse(new Cursor("dog")), 'd', 0, 1);
        assertEquals('e', p.parse("egg"));
        assertEquals('f', p.parse("father"));
        assertThrows(ParseException.class, () -> p.parse(new Cursor("apple")));
        assertThrows(ParseException.class, () -> p.parse(new Cursor("high")));
        assertThrows(ParseException.class, () -> p.parse(new Cursor("")));
    }

    @Test
    public void testRange2() {
        Parser<Character> p = range('f', 'd');
        verifyParseResult(p.parse(new Cursor("dog")), 'd', 0, 1);
        assertEquals('e', p.parse("egg"));
        assertEquals('f', p.parse("father"));
        assertThrows(ParseException.class, () -> p.parse(new Cursor("apple")));
        assertThrows(ParseException.class, () -> p.parse(new Cursor("high")));
        assertThrows(ParseException.class, () -> p.parse(new Cursor("")));
    }

    @Test
    public void testChs1() {
        Parser<Character> p = chs('f', 'o', 'h');
        verifyParseResult(p.parse(new Cursor("far")), 'f', 0, 1);
        assertEquals('o', p.parse("ohh"));
        assertEquals('h', p.parse("high"));
        assertThrows(ParseException.class, () -> p.parse("byx"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testChs2() {
        Parser<Character> p = chs('f');
        verifyParseResult(p.parse(new Cursor("far")), 'f', 0, 1);
        assertThrows(ParseException.class, () -> p.parse("byx"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testChs3() {
        Parser<Character> p = chs();
        assertThrows(ParseException.class, () -> p.parse("byx"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testNot1() {
        Parser<Character> p = not('f', 'o', 'h');
        verifyParseResult(p.parse(new Cursor("byx")), 'b', 0, 1);
        assertThrows(ParseException.class, () -> p.parse("fog"));
        assertThrows(ParseException.class, () -> p.parse("ohhh"));
        assertThrows(ParseException.class, () -> p.parse("high"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testNot2() {
        Parser<Character> p = not('f');
        verifyParseResult(p.parse(new Cursor("byx")), 'b', 0, 1);
        assertThrows(ParseException.class, () -> p.parse("fog"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testNot3() {
        Parser<Character> p = not();
        verifyParseResult(p.parse(new Cursor("byx")), 'b', 0, 1);
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testStr() {
        Parser<String> p = str("byx");
        verifyParseResult(p.parse(new Cursor("byxabcd")), "byx", 0, 3);
        verifyParseResult(p.parse(new Cursor("byx")), "byx", 0, 3);
        assertThrows(ParseException.class, () -> p.parse("by"));
        assertThrows(ParseException.class, () -> p.parse("bytb"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testStrs1() {
        Parser<String> p = strs("apple", "amend", "byx");
        verifyParseResult(p.parse(new Cursor("applemen")), "apple", 0, 5);
        verifyParseResult(p.parse(new Cursor("byxm")), "byx", 0, 3);
        assertThrows(ParseException.class, () -> p.parse("app"));
        assertThrows(ParseException.class, () -> p.parse("bycd"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testStrs2() {
        Parser<String> p = strs("byx");
        verifyParseResult(p.parse(new Cursor("byxabcd")), "byx", 0, 3);
        verifyParseResult(p.parse(new Cursor("byx")), "byx", 0, 3);
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
        verifyParseResult(p.parse(new Cursor("helloabc")), new Pair<>("hello", 'a'), 0, 6);
        assertThrows(ParseException.class, () -> p.parse("hello world"));
        assertThrows(ParseException.class, () -> p.parse("byx"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testAnd2() {
        Parser<Pair<String, Character>> p = str("hello").and('a');
        verifyParseResult(p.parse(new Cursor("helloabc")), new Pair<>("hello", 'a'), 0, 6);
        assertThrows(ParseException.class, () -> p.parse("hello world"));
        assertThrows(ParseException.class, () -> p.parse("byx"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testAnd3() {
        Parser<Pair<String, String>> p = str("hello").and("abc");
        verifyParseResult(p.parse(new Cursor("helloabc")), new Pair<>("hello", "abc"), 0, 8);
        assertThrows(ParseException.class, () -> p.parse("hello world"));
        assertThrows(ParseException.class, () -> p.parse("byx"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testSeq1() {
        Parser<List<Object>> p = seq(ch('a'), str("bcd"), ch('e'));
        verifyParseResult(p.parse(new Cursor("abcdefgh")), List.of('a', "bcd", 'e'), 0, 5);
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
        verifyParseResult(p.parse(new Cursor("abcde")), List.of("abc"), 0, 3);
        assertThrows(ParseException.class, () -> p.parse("axy"));
        assertThrows(ParseException.class, () -> p.parse("ab"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testSeq3() {
        Parser<List<Object>> p = seq();
        verifyParseResult(p.parse(new Cursor("abcde")), Collections.emptyList(), 0, 0);
        ParseResult<List<Object>> r2 = p.parse(new Cursor(""));
        assertEquals(Collections.emptyList(), r2.getResult());
        assertEquals(0, r2.getBefore().index());
        assertEquals(0, r2.getRemain().index());
    }

    @Test
    public void testOr() {
        Parser<Character> p = ch('a').or(ch('b'));
        verifyParseResult(p.parse(new Cursor("a")), 'a', 0, 1);
        verifyParseResult(p.parse(new Cursor("b")), 'b', 0, 1);
        assertThrows(ParseException.class, () -> p.parse("x"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testOneOf1() {
        Parser<Character> p = oneOf(ch('a'), ch('b'), ch('c'));
        verifyParseResult(p.parse(new Cursor("a")), 'a', 0, 1);
        verifyParseResult(p.parse(new Cursor("b")), 'b', 0, 1);
        verifyParseResult(p.parse(new Cursor("c")), 'c', 0, 1);
        assertThrows(ParseException.class, () -> p.parse("d"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testOneOf2() {
        Parser<Character> p = oneOf(ch('a'));
        verifyParseResult(p.parse(new Cursor("a")), 'a', 0, 1);
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
    public void testAlt() {
        Parser<Object> p = alt(str("hello"), ch('a'), ch('b'));
        verifyParseResult(p.parse(new Cursor("hello")), "hello", 0, 5);
        verifyParseResult(p.parse(new Cursor("a")), 'a', 0, 1);
        verifyParseResult(p.parse(new Cursor("b")), 'b', 0, 1);
    }

    @Test
    public void testMap() {
        Parser<Integer> p = str("hello").map(String::length);
        verifyParseResult(p.parse(new Cursor("hello")), 5, 0, 5);
        assertThrows(ParseException.class, () -> p.parse("hi"));
    }

    @Test
    public void testMapException1() {
        Parser<String> p = str("hello").mapException((cursor, throwable) -> {
            assertEquals(0, cursor.index());
            assertTrue(throwable instanceof ParseException);
            return new MyParseException(cursor, "byx");
        });

        verifyParseResult(p.parse(new Cursor("hello")), "hello", 0, 5);
        assertThrows(MyParseException.class, () -> p.parse("hi"));
    }

    @Test
    public void testMapException2() {
        Parser<String> p = str("hello").mapException(throwable -> {
            assertTrue(throwable instanceof ParseException);
            return new MyParseException(null, "byx");
        });

        verifyParseResult(p.parse(new Cursor("hello")), "hello", 0, 5);
        assertThrows(MyParseException.class, () -> p.parse("hi"));
    }

    @Test
    public void testMany() {
        Parser<List<Character>> p = ch('a').many();
        verifyParseResult(p.parse(new Cursor("")), Collections.emptyList(), 0, 0);
        verifyParseResult(p.parse(new Cursor("bbb")), Collections.emptyList(), 0, 0);
        verifyParseResult(p.parse(new Cursor("a")), List.of('a'), 0, 1);
        verifyParseResult(p.parse(new Cursor("aaa")), List.of('a', 'a', 'a'), 0, 3);
    }

    @Test
    public void testMany1() {
        Parser<List<Character>> p = ch('a').many1();
        verifyParseResult(p.parse(new Cursor("a")), List.of('a'), 0, 1);
        verifyParseResult(p.parse(new Cursor("aaa")), List.of('a', 'a', 'a'), 0, 3);
        assertThrows(ParseException.class, () -> p.parse("bbb"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testRepeat() {
        Parser<List<Character>> p = ch('a').repeat(3);
        verifyParseResult(p.parse(new Cursor("aaa")), List.of('a', 'a', 'a'), 0, 3);
        assertThrows(ParseException.class, () -> p.parse("a"));
        assertThrows(ParseException.class, () -> p.parse("aa"));
        assertThrows(ParseException.class, () -> p.parse("bbb"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testOpt1() {
        Parser<Character> p = ch('a').opt('x');
        verifyParseResult(p.parse(new Cursor("a")), 'a', 0, 1);
        verifyParseResult(p.parse(new Cursor("byx")), 'x', 0, 0);
        verifyParseResult(p.parse(new Cursor("")), 'x', 0, 0);
    }

    @Test
    public void testOpt2() {
        Parser<Character> p = ch('a').opt();
        verifyParseResult(p.parse(new Cursor("a")), 'a', 0, 1);
        verifyParseResult(p.parse(new Cursor("byx")), null, 0, 0);
        verifyParseResult(p.parse(new Cursor("")), null, 0, 0);
    }

    @Test
    public void testLazy() {
        int[] val = {0};
        Parser<Character> p = lazy(() -> {
            val[0] = 1;
            return ch('a');
        });
        assertEquals(0, val[0]);
        verifyParseResult(p.parse(new Cursor("a")), 'a', 0, 1);
        assertEquals(1, val[0]);
    }

    @Test
    public void testSurround1() {
        Parser<Character> p = ch('a').surround(ch('('), ch(')'));
        verifyParseResult(p.parse(new Cursor("(a)")), 'a', 0, 3);
        assertThrows(ParseException.class, () -> p.parse("(a"));
        assertThrows(ParseException.class, () -> p.parse("a)"));
        assertThrows(ParseException.class, () -> p.parse("a"));
        assertThrows(ParseException.class, () -> p.parse("(b)"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testSurround2() {
        Parser<Character> p = ch('a').surround(str("***"));
        verifyParseResult(p.parse(new Cursor("***a***")), 'a', 0, 7);
        assertThrows(ParseException.class, () -> p.parse("***a**"));
        assertThrows(ParseException.class, () -> p.parse("*a***"));
        assertThrows(ParseException.class, () -> p.parse("*a**"));
        assertThrows(ParseException.class, () -> p.parse("***b***"));
        assertThrows(ParseException.class, () -> p.parse(""));
    }

    @Test
    public void testSkipFirst() {
        Parser<String> p = skip(ch('a')).and(str("bc"));
        verifyParseResult(p.parse(new Cursor("abc")), "bc", 0, 3);
    }

    @Test
    public void testSkipSecond() {
        Parser<Character> p = ch('a').skip(str("bc"));
        verifyParseResult(p.parse(new Cursor("abc")), 'a', 0, 3);
    }

    @Test
    public void testSkip() {
        Parser<String> p = skip(ch('a')).and(str("bc"));
        verifyParseResult(p.parse(new Cursor("abc")), "bc", 0, 3);
    }

    @Test
    public void testThen() {
        Set<String> keywords = Set.of("if", "else", "while", "for");
        Parser<Character> alpha = range('a', 'z').or(range('A', 'Z'));
        Parser<Character> digit = range('0', '9');
        Parser<Character> underline = ch('_');
        Parser<String> identifier = oneOf(alpha, underline)
                .and(oneOf(digit, alpha, underline).many())
                .map(p -> p.getFirst() + p.getSecond().stream().map(Objects::toString).collect(Collectors.joining()))
                .then(r -> keywords.contains(r.getResult())
                        ? fail(input -> new MyParseException(r.getBefore(), "关键字不能作为标识符"))
                        : success(r.getResult()));
        assertEquals("nums", identifier.parse("nums"));
        assertThrows(MyParseException.class, () -> identifier.parse("while"));
    }

    @Test
    public void testFatal1() {
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
    public void testFatal2() {
        Parser<Character> p = ch('a').fatal((c, e) -> {
            assertEquals(0, c.index());
            assertNotNull(e);
            return new MyParseException(c, "");
        });
        assertThrows(MyParseException.class, () -> p.parse("bcd"));
    }

    @Test
    public void testFollow() {
        Parser<String> p = str("abc").follow(ch('d'));
        verifyParseResult(p.parse(new Cursor("abcd")), "abc", 0, 3);
        assertThrows(ParseException.class, () -> p.parse("abcx"));
        assertThrows(ParseException.class, () -> p.parse("abc"));
    }

    @Test
    public void testNotFollow() {
        Parser<String> p = str("abc").notFollow(ch('d'));
        verifyParseResult(p.parse(new Cursor("abcx")), "abc", 0, 3);
        verifyParseResult(p.parse(new Cursor("abc")), "abc", 0, 3);
        assertThrows(ParseException.class, () -> p.parse("abcd"));
    }
}
