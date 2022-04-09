package byx.parserc;

import static byx.parserc.Parsers.*;

/**
 * 布尔表达式计算器
 */
public class BoolExprCalc {
    private static final Parser<Boolean> trueValue = ch('t').map(c -> true);
    private static final Parser<Boolean> falseValue = ch('f').map(c -> false);
    private static final Parser<Boolean> andExpr = seq(
            string("&("),
            separateBy(ch(','), lazy(BoolExprCalc::getBoolExpr)).ignoreDelimiter(),
            ch(')'),
            (a, b, c) -> b.stream().reduce(true, Boolean::logicalAnd)
    );
    private static final Parser<Boolean> orExpr = seq(
            string("|("),
            separateBy(ch(','), lazy(BoolExprCalc::getBoolExpr)).ignoreDelimiter(),
            ch(')'),
            (a, b, c) -> b.stream().reduce(false, Boolean::logicalOr)
    );
    private static final Parser<Boolean> notExpr = seq(
            string("!("),
            lazy(BoolExprCalc::getBoolExpr),
            ch(')'),
            (a, b, c) -> !b
    );
    private static final Parser<Boolean> boolExpr = oneOf(
            trueValue,
            falseValue,
            andExpr,
            orExpr,
            notExpr
    );

    private static Parser<Boolean> getBoolExpr() {
        return boolExpr;
    }

    public static Boolean eval(String s) {
        return boolExpr.parse(s);
    }
}
