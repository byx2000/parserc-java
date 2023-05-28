package byx.parserc;

import java.util.ArrayList;
import java.util.List;

import static byx.parserc.Parsers.*;

/**
 * 布尔表达式计算器
 */
public class BoolExprCalc {
    private static final Parser<?> lp = ch('(').fatal(c -> new MyParseException("'(' expected"));
    private static final Parser<?> rp = ch(')').fatal(c -> new MyParseException("')' expected"));
    private static final Parser<Boolean> lazyBoolExpr = lazy(() -> BoolExprCalc.boolExpr);
    private static final Parser<Boolean> trueValue = ch('t').value(true);
    private static final Parser<Boolean> falseValue = ch('f').value(false);
    private static final Parser<List<Boolean>> boolExprList = lazyBoolExpr.and(skip(ch(',')).and(lazyBoolExpr).many())
            .map(r -> reduceList(r.getFirst(), r.getSecond()));
    private static final Parser<Boolean> andExpr = skip(ch('&').and(lp)).and(boolExprList).skip(rp)
            .map(r -> r.stream().reduce(true, Boolean::logicalAnd));
    private static final Parser<Boolean> orExpr = skip(ch('|').and(lp)).and(boolExprList).skip(rp)
            .map(r -> r.stream().reduce(false, Boolean::logicalOr));
    private static final Parser<Boolean> notExpr = skip(ch('!').and(lp)).and(lazyBoolExpr).skip(rp).map(v -> !v);
    private static final Parser<Boolean> boolExpr = oneOf(trueValue, falseValue, andExpr, orExpr, notExpr)
            .fatal(c -> new MyParseException("expected one of: t, f, &(...), |(...), !(...)"));
    private static final Parser<Boolean> parser = boolExpr.skip(end()
            .fatal(c -> new MyParseException("redundant character at the end of input")));

    public static Boolean eval(String s) {
        return parser.parse(s);
    }

    private static <T> List<T> reduceList(T first, List<T> remain) {
        List<T> list = new ArrayList<>();
        list.add(first);
        list.addAll(remain);
        return list;
    }
}
