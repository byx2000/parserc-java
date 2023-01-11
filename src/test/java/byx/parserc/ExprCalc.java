package byx.parserc;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static byx.parserc.Parsers.*;

/**
 * 表达式计算器
 */
public class ExprCalc {
    private static final Parser<?> w = chs(' ', '\t', '\r', '\n');
    private static final Parser<?> ws = w.many();
    private static final Parser<?> digit = range('0', '9');
    private static final Parser<Character> add = ch('+').surround(ws);
    private static final Parser<Character> sub = ch('-').surround(ws);
    private static final Parser<Character> mul = ch('*').surround(ws);
    private static final Parser<Character> div = ch('/').surround(ws);
    private static final Parser<Character> lp = ch('(').surround(ws);
    private static final Parser<Character> rp = ch(')').surround(ws);
    private static final Parser<String> digits = digit.many1().map(ExprCalc::join);
    private static final Parser<Double> integer = digits.map(Double::parseDouble);
    private static final Parser<Double> decimal = seq(digits, ch('.'), digits).map(ExprCalc::join).map(Double::parseDouble);
    private static final Parser<Double> number = decimal.or(integer).surround(ws);
    private static final Parser<Double> bracketExpr = skip(lp).and(lazy(() -> ExprCalc.expr)).skip(rp);
    private static final Parser<Double> negExpr = skip(sub).and(lazy(() -> ExprCalc.fact)).map(e -> -e);
    private static final Parser<Double> fact = oneOf(number, bracketExpr, negExpr);
    private static final Parser<Double> term = fact.and(mul.or(div).and(fact).many()).map(ExprCalc::calc);
    private static final Parser<Double> expr = term.and(add.or(sub).and(term).many()).map(ExprCalc::calc);
    private static final Parser<Double> parser = expr.end();

    private static String join(List<?> list) {
        return list.stream().map(Objects::toString).collect(Collectors.joining());
    }

    private static Double calc(Pair<Double, List<Pair<Character, Double>>> p) {
        double res = p.getFirst();
        for (Pair<Character, Double> pp : p.getSecond()) {
            switch (pp.getFirst()) {
                case '+':
                    res += pp.getSecond();
                    break;
                case '-':
                    res -= pp.getSecond();
                    break;
                case '*':
                    res *= pp.getSecond();
                    break;
                case '/':
                    res /= pp.getSecond();
                    break;
            }
        }
        return res;
    }

    public static Double eval(String input) {
        return parser.parse(input);
    }
}
