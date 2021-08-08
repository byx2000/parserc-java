package byx.parserc.example;

import byx.parserc.Cursor;
import byx.parserc.DelegateParser;
import byx.parserc.ParseException;
import byx.parserc.Parser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static byx.parserc.Parsers.*;

public class Example2Test {
    @Test
    public void test() throws ParseException {
        DelegateParser<Boolean, Character> e = new DelegateParser<>();
        Parser<Boolean, Character> e1 = one('t').map(c -> true);
        Parser<Boolean, Character> e2 = one('f').map(c -> false);
        Parser<Boolean, Character> e3 = skip(literal("&(")).concat(e).concat(skip(one(',')).concat(e).zeroOrMore()).skip(one(')'))
                .map(p -> p.getSecond().stream().reduce(p.getFirst(), (a, b) -> a & b));
        Parser<Boolean, Character> e4 = skip(literal("|(")).concat(e).concat(skip(one(',')).concat(e).zeroOrMore()).skip(one(')'))
                .map(p -> p.getSecond().stream().reduce(p.getFirst(), (a, b) -> a | b));
        Parser<Boolean, Character> e5 = skip(literal("!(")).concat(e).skip(one(')')).map(b -> !b);
        e.set(e1.or(e2).or(e3).or(e4).or(e5));

        assertTrue(e.parse(Cursor.of("t")).getResult());
        assertFalse(e.parse(Cursor.of("f")).getResult());
        assertFalse(e.parse(Cursor.of("!(t)")).getResult());
        assertTrue(e.parse(Cursor.of("!(f)")).getResult());
        assertTrue(e.parse(Cursor.of("&(t)")).getResult());
        assertFalse(e.parse(Cursor.of("&(f)")).getResult());
        assertTrue(e.parse(Cursor.of("|(t)")).getResult());
        assertFalse(e.parse(Cursor.of("|(f)")).getResult());
        assertTrue(e.parse(Cursor.of("&(t,t)")).getResult());
        assertFalse(e.parse(Cursor.of("&(t,f)")).getResult());
        assertFalse(e.parse(Cursor.of("&(f,t)")).getResult());
        assertFalse(e.parse(Cursor.of("&(f,f)")).getResult());
        assertTrue(e.parse(Cursor.of("|(t,t)")).getResult());
        assertTrue(e.parse(Cursor.of("|(t,f)")).getResult());
        assertTrue(e.parse(Cursor.of("|(f,t)")).getResult());
        assertFalse(e.parse(Cursor.of("|(f,f)")).getResult());
        assertFalse(e.parse(Cursor.of("|(&(t,f,t),!(t))")).getResult());
        assertTrue(e.parse(Cursor.of("!(&(!(t),&(f),|(f)))")).getResult());
    }
}
