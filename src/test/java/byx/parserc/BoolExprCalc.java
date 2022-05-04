package byx.parserc;

import static byx.parserc.Parsers.*;

/**
 * 布尔表达式计算器
 */
public class BoolExprCalc {
    private static final Parser<Boolean> lazyBoolExpr = lazy(BoolExprCalc::getBoolExpr);
    private static final Parser<Boolean> trueValue = ch('t').map(true);
    private static final Parser<Boolean> falseValue = ch('f').map(false);
    private static final Parser<Boolean> andExpr = skip(str("&(")).and(separate(ch(','), lazyBoolExpr)).skip(ch(')'))
            .map(r -> r.stream().reduce(true, Boolean::logicalAnd));
    private static final Parser<Boolean> orExpr = skip(str("|(")).and(separate(ch(','), lazyBoolExpr)).skip(ch(')'))
            .map(r -> r.stream().reduce(false, Boolean::logicalOr));
    private static final Parser<Boolean> notExpr = skip(str("!(")).and(lazyBoolExpr).skip(ch(')')).map(v -> !v);
    private static final Parser<Boolean> boolExpr = oneOf(trueValue, falseValue, andExpr, orExpr, notExpr);

    private static Parser<Boolean> getBoolExpr() {
        return boolExpr;
    }

    public static Boolean eval(String s) {
        return boolExpr.parse(s);
    }
}
