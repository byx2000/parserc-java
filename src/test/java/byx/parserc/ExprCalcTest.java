package byx.parserc;

import byx.parserc.exception.ParseInternalException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static byx.parserc.Parsers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 表达式计算器
 */
class ExprCalc {
    private static final Parser<?> digit = range('0', '9');
    private static final Parser<Character> add = ch('+').trim();
    private static final Parser<Character> sub = ch('-').trim();
    private static final Parser<Character> mul = ch('*').trim();
    private static final Parser<Character> div = ch('/').trim();
    private static final Parser<Character> lp = ch('(').trim();
    private static final Parser<Character> rp = ch(')').trim();
    private static final Parser<String> digits = digit.many1().map(ExprCalc::join);
    private static final Parser<Double> integer = digits.map(Double::parseDouble);
    private static final Parser<Double> decimal = seq(digits, ch('.'), digits).map(ExprCalc::join).map(Double::parseDouble);
    private static final Parser<Double> number = decimal.or(integer).trim();
    private static final Parser<Double> bracketExpr = skip(lp).and(lazy(() -> ExprCalc.expr)).skip(rp);
    private static final Parser<Double> negFact = skip(sub).and(lazy(() -> ExprCalc.fact)).map(e -> -e);
    private static final Parser<Double> fact = oneOf(number, bracketExpr, negFact);
    private static final Parser<Double> term = fact.and(mul.or(div).and(fact).many()).map(ExprCalc::calc);
    private static final Parser<Double> expr = term.and(add.or(sub).and(term).many()).map(ExprCalc::calc)
        .fatal(() -> new MyParseException("illegal arithmetic expr"));

    private static String join(List<?> list) {
        return list.stream().map(Objects::toString).collect(Collectors.joining());
    }

    private static Double calc(Pair<Double, List<Pair<Character, Double>>> p) {
        double res = p.first();
        for (Pair<Character, Double> pp : p.second()) {
            switch (pp.first()) {
                case '+':
                    res += pp.second();
                    break;
                case '-':
                    res -= pp.second();
                    break;
                case '*':
                    res *= pp.second();
                    break;
                case '/':
                    res /= pp.second();
                    break;
            }
        }
        return res;
    }

    public static Double eval(String s) {
        ParseResult<Double> r = expr.parse(s, 0);
        if (r.index() != s.length()) {
            throw new MyParseException(s, r.index(), "redundant character at the end of expr");
        }
        return r.result();
    }
}

public class ExprCalcTest {
    @Test
    public void test() throws ParseInternalException {
        assertEquals(1.0, ExprCalc.eval("1"));
        assertEquals(1.0, ExprCalc.eval(" 1"));
        assertEquals(1.0, ExprCalc.eval("1\t"));
        assertEquals(123.0, ExprCalc.eval("\n123"));
        assertEquals(123.0, ExprCalc.eval("123 "));
        assertEquals(3.14, ExprCalc.eval(" 3.14"));
        assertEquals(-1.0, ExprCalc.eval("-1"));
        assertEquals(-123.0, ExprCalc.eval("-123 "));
        assertEquals(-3.14, ExprCalc.eval("-3.14 "));
        assertEquals(2.0 + 3.0, ExprCalc.eval("2+3"));
        assertEquals(5.2 - 7.56, ExprCalc.eval("5.2-7.56"));
        assertEquals(123.456 * 67.89, ExprCalc.eval("123.456*67.89"));
        assertEquals(0.78 / 10.4, ExprCalc.eval(" 0.78 / 10.4 "));
        assertEquals((2.0 + 3) * (7 - 4.0), ExprCalc.eval("(2+3)*(7-4)"));
        assertEquals(2.4 / 5.774 * (6 / 3.57 + 6.37) - 2 * 7 / 5.2 + 5, ExprCalc.eval("2.4 / 5.774 * (6 / 3.57 + 6.37) - 2 * 7 / 5.2 + 5"));
        assertEquals(77.58 * (6 / 3.14 + 55.2234) - 2 * 6.1 / (1.0 + 2 / (4.0 - 3.8 * 5)), ExprCalc.eval("77.58* ( 6 / 3.14+55.2234 ) -2 * 6.1/ ( 1.0+2/ (4.0-3.8*5))  "));

        assertThrows(MyParseException.class, () -> ExprCalc.eval(""));
        assertThrows(MyParseException.class, () -> ExprCalc.eval("abc"));
        assertThrows(MyParseException.class, () -> ExprCalc.eval("12.34.56"));
        assertThrows(MyParseException.class, () -> ExprCalc.eval("2+"));
        assertThrows(MyParseException.class, () -> ExprCalc.eval("-2+3-"));
        assertThrows(MyParseException.class, () -> ExprCalc.eval("2.5+*3/5"));
        assertThrows(MyParseException.class, () -> ExprCalc.eval("()"));
        assertThrows(MyParseException.class, () -> ExprCalc.eval("("));
        assertThrows(MyParseException.class, () -> ExprCalc.eval(")"));
        assertThrows(MyParseException.class, () -> ExprCalc.eval("2*(4+(3/5)"));
    }
}
