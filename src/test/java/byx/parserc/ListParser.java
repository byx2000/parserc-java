package byx.parserc;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static byx.parserc.Parsers.*;

/**
 * 列表解析器
 */
public class ListParser {
    private static final Parser<Character> w = chs(' ', '\t', '\r', '\n');
    private static final Parser<List<Character>> ws = w.many();
    private static final Parser<Character> digit = range('0', '9');
    private static final Parser<String> digits = digit.many1().map(ListParser::join);
    private static final Parser<Integer> integer = digits.map(Integer::parseInt).surroundBy(ws);
    private static final Parser<Double> decimal = seq(digits, ch('.'), digits, (a, b, c) -> Double.parseDouble(a + b + c)).surroundBy(ws);
    private static final Parser<String> string = seq(
            ch('\''),
            not('\'').many(),
            ch('\''),
            (a, b, c) -> join(b)
    ).surroundBy(ws);
    private static final Parser<Character> lp = ch('[').surroundBy(ws);
    private static final Parser<Character> rp = ch(']').surroundBy(ws);
    private static final Parser<Character> comma = ch(',').surroundBy(ws);
    private static final Parser<Object> listItem = oneOf(
            decimal.mapTo(Object.class),
            integer.mapTo(Object.class),
            string.mapTo(Object.class),
            lazy(ListParser::getList).mapTo(Object.class)
    );
    private static final Parser<List<Object>> emptyList = seq(lp, rp, (a, b) -> Collections.emptyList());
    private static final Parser<List<Object>> list = oneOf(
            emptyList,
            separateBy(comma, listItem).ignoreDelimiter().surroundBy(lp, rp)
    );

    private static String join(List<?> list) {
        return list.stream().map(Objects::toString).collect(Collectors.joining());
    }

    private static Parser<List<Object>> getList() {
        return list;
    }

    public static List<Object> parse(String s) {
        return list.parse(s);
    }
}
