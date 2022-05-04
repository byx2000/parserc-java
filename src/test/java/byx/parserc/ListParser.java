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
    private static final Parser<Integer> integer = digits.map(Integer::parseInt).surround(ws);
    private static final Parser<Double> decimal = seq(digits, ch('.'), digits).map(ListParser::join).map(Double::parseDouble);
    private static final Parser<String> string = skip(ch('\'')).and(not('\'').many()).skip(ch('\'')).map(ListParser::join);
    private static final Parser<Character> lp = ch('[').surround(ws);
    private static final Parser<Character> rp = ch(']').surround(ws);
    private static final Parser<Character> comma = ch(',').surround(ws);
    private static final Parser<Object> listItem = oneOf(
            decimal.mapTo(Object.class),
            integer.mapTo(Object.class),
            string.mapTo(Object.class),
            lazy(ListParser::getList).mapTo(Object.class)
    );
    private static final Parser<List<Object>> list = skip(lp).and(separate(comma, listItem).opt(Collections.emptyList())).skip(rp);

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
