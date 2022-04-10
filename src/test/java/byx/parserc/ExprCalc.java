package byx.parserc;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static byx.parserc.Parsers.*;

/**
 * 表达式计算器
 */
public class ExprCalc {
    private static final Parser<Character> w = chs(' ', '\t', '\r', '\n');
    private static final Parser<List<Character>> ws = w.many();
    private static final Parser<Character> digit = range('0', '9');
    private static final Parser<Character> add = ch('+').surroundBy(ws);
    private static final Parser<Character> sub = ch('-').surroundBy(ws);
    private static final Parser<Character> mul = ch('*').surroundBy(ws);
    private static final Parser<Character> div = ch('/').surroundBy(ws);
    private static final Parser<Character> lp = ch('(').surroundBy(ws);
    private static final Parser<Character> rp = ch(')').surroundBy(ws);
    private static final Parser<String> digits = digit.many1().map(ExprCalc::join);
    private static final Parser<Double> integer = digits.map(Double::parseDouble);
    private static final Parser<Double> decimal = seq(digits, ch('.'), digits, (a, b, c) -> Double.parseDouble(a + b + c));
    private static final Parser<Double> number = decimal.or(integer).surroundBy(ws);
    private static final Parser<Double> bracketExpr = lazy(ExprCalc::getExpr).surroundBy(lp, rp);
    private static final Parser<Double> signExpr = seq(add.or(sub), lazy(ExprCalc::getFact), (s, v) -> s == '-' ? -v : v);
    private static final Parser<Double> fact = oneOf(number, bracketExpr, signExpr);
    private static final Parser<Double> term = separateBy(mul.or(div), fact).map(ExprCalc::calc);
    private static final Parser<Double> expr = separateBy(add.or(sub), term).map(ExprCalc::calc);
    
    private static Parser<Double> getFact() {
        return fact;
    }

    private static Parser<Double> getExpr() {
        return expr;
    }

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
        return expr.parse(input);
    }
}
