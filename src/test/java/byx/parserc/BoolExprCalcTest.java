package byx.parserc;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static byx.parserc.Parsers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 布尔表达式计算器
 */
class BoolExprCalc {
    private static final Parser<?> lp = ch('(').fatal((s, i) -> new MyParseException(s, i, "'(' expected"));
    private static final Parser<?> rp = ch(')').fatal((s, i) -> new MyParseException(s, i, "')' expected"));
    private static final Parser<Boolean> boolExpr = oneOf(
        lazy(() -> BoolExprCalc.trueValue),
        lazy(() -> BoolExprCalc.falseValue),
        lazy(() -> BoolExprCalc.andExpr),
        lazy(() -> BoolExprCalc.orExpr),
        lazy(() -> BoolExprCalc.notExpr)
    ).fatal((s, i) -> new MyParseException(s, i, "expected one of: t, f, &(...), |(...), !(...)"));
    private static final Parser<Boolean> trueValue = ch('t').value(true);
    private static final Parser<Boolean> falseValue = ch('f').value(false);
    private static final Parser<List<Boolean>> boolExprList = boolExpr.and(skip(ch(',')).and(boolExpr).many())
        .map(r -> reduceList(r.first(), r.second()));
    private static final Parser<Boolean> andExpr = skip(ch('&').and(lp)).and(boolExprList).skip(rp)
        .map(r -> r.stream().reduce(true, Boolean::logicalAnd));
    private static final Parser<Boolean> orExpr = skip(ch('|').and(lp)).and(boolExprList).skip(rp)
        .map(r -> r.stream().reduce(false, Boolean::logicalOr));
    private static final Parser<Boolean> notExpr = skip(ch('!').and(lp)).and(boolExpr).skip(rp).map(v -> !v);


    private static <T> List<T> reduceList(T first, List<T> remain) {
        List<T> list = new ArrayList<>();
        list.add(first);
        list.addAll(remain);
        return list;
    }

    public static boolean eval(String s) {
        ParseResult<Boolean> r = boolExpr.parse(s, 0);
        if (r.index() != s.length()) {
            throw new MyParseException(s, r.index(), "redundant character at the end of expr");
        }
        return r.result();
    }
}

public class BoolExprCalcTest {
    @Test
    public void test() {
        assertTrue(BoolExprCalc.eval("t"));
        assertFalse(BoolExprCalc.eval("f"));
        assertFalse(BoolExprCalc.eval("!(t)"));
        assertTrue(BoolExprCalc.eval("!(f)"));
        assertTrue(BoolExprCalc.eval("&(t)"));
        assertFalse(BoolExprCalc.eval("&(f)"));
        assertTrue(BoolExprCalc.eval("|(t)"));
        assertFalse(BoolExprCalc.eval("|(f)"));
        assertTrue(BoolExprCalc.eval("&(t,t)"));
        assertFalse(BoolExprCalc.eval("&(t,f)"));
        assertFalse(BoolExprCalc.eval("&(f,t)"));
        assertFalse(BoolExprCalc.eval("&(f,f)"));
        assertTrue(BoolExprCalc.eval("|(t,t)"));
        assertTrue(BoolExprCalc.eval("|(t,f)"));
        assertTrue(BoolExprCalc.eval("|(f,t)"));
        assertFalse(BoolExprCalc.eval("|(f,f)"));
        assertFalse(BoolExprCalc.eval("|(&(t,f,t),!(t))"));
        assertTrue(BoolExprCalc.eval("!(&(!(t),&(f),|(f)))"));

        assertThrows(MyParseException.class, () -> BoolExprCalc.eval("a"));
        assertThrows(MyParseException.class, () -> BoolExprCalc.eval("ff"));
        assertThrows(MyParseException.class, () -> BoolExprCalc.eval("ft"));
        assertThrows(MyParseException.class, () -> BoolExprCalc.eval("&f,t"));
        assertThrows(MyParseException.class, () -> BoolExprCalc.eval("|(f,t))"));
        assertThrows(MyParseException.class, () -> BoolExprCalc.eval("+(t,f)"));
        assertThrows(MyParseException.class, () -> BoolExprCalc.eval("!(t,t)"));
        assertThrows(MyParseException.class, () -> BoolExprCalc.eval("&(f t)"));
        assertThrows(MyParseException.class, () -> BoolExprCalc.eval("!(&(!(t),&((f),|(f)))"));
        assertThrows(MyParseException.class, () -> BoolExprCalc.eval("!(&(!(t),&(f),|(f))))"));
    }
}
