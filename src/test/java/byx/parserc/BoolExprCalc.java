package byx.parserc;

import static byx.parserc.Parsers.*;

/**
 * 布尔表达式计算器
 */
public class BoolExprCalc {
    private static final Parser<?> lp = ch('(').fatal("'(' expected");
    private static final Parser<?> rp = ch(')').fatal("')' expected");
    private static final Parser<Boolean> lazyBoolExpr = lazy(() -> BoolExprCalc.boolExpr);
    private static final Parser<Boolean> trueValue = ch('t').map(true);
    private static final Parser<Boolean> falseValue = ch('f').map(false);
    private static final Parser<Boolean> andExpr = skip(ch('&').and(lp)).and(list(ch(','), lazyBoolExpr)).skip(rp)
            .map(r -> r.stream().reduce(true, Boolean::logicalAnd));
    private static final Parser<Boolean> orExpr = skip(ch('|').and(lp)).and(list(ch(','), lazyBoolExpr)).skip(rp)
            .map(r -> r.stream().reduce(false, Boolean::logicalOr));
    private static final Parser<Boolean> notExpr = skip(ch('!').and(lp)).and(lazyBoolExpr).skip(rp).map(v -> !v);
    private static final Parser<Boolean> boolExpr = oneOf(trueValue, falseValue, andExpr, orExpr, notExpr)
            .fatal("expected one of: t, f, &(...), |(...), !(...)");
    private static final Parser<Boolean> parser = boolExpr.skip(end().fatal("redundant character at the end of input"));

    public static Boolean eval(String s) {
        return parser.parse(s);
    }
}
