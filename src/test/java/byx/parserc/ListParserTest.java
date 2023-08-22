package byx.parserc;

import byx.parserc.exception.ParseException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static byx.parserc.Parsers.*;
import static byx.parserc.Parsers.skip;
import static org.junit.jupiter.api.Assertions.*;

public class ListParserTest {
    @Test
    public void test() {
        assertEquals(List.of(), ListParser.parse("[]"));
        assertEquals(List.of(123), ListParser.parse("[123]"));
        assertEquals(List.of(3.14), ListParser.parse("[3.14]"));
        assertEquals(List.of(123), ListParser.parse("[123]"));
        assertEquals(List.of("hello"), ListParser.parse("['hello']"));
        assertEquals(List.of(List.of(1, 2, 3)), ListParser.parse("[[1, 2, 3]]"));
        assertEquals(List.of(123, 3.14, "hello", List.of(1, 2, 3)), ListParser.parse("[123, 3.14, 'hello', [1, 2, 3]]"));

        assertThrows(ParseException.class, () -> ListParser.parse("["));
        assertThrows(ParseException.class, () -> ListParser.parse("]"));
        assertThrows(ParseException.class, () -> ListParser.parse("[123, 3.14, 'hello'"));
        assertThrows(ParseException.class, () -> ListParser.parse("123, 3.14, 'hello']"));
        assertThrows(ParseException.class, () -> ListParser.parse("[[123, 3.14, 'hello']"));
        assertThrows(ParseException.class, () -> ListParser.parse("[123, 3.14, 'hello']]"));
        assertThrows(ParseException.class, () -> ListParser.parse("[123 3.14, 'hello']"));
    }
}

/**
 * 列表解析器
 */
class ListParser {
    private static final Parser<Character> digit = range('0', '9');
    private static final Parser<String> digits = digit.many1().map(ListParser::join);
    private static final Parser<Integer> integer = digits.map(Integer::parseInt).trim();
    private static final Parser<Double> decimal = seq(digits, ch('.'), digits).map(ListParser::join).map(Double::parseDouble);
    private static final Parser<String> string = skip(ch('\'')).and(not('\'').many()).skip(ch('\'')).map(ListParser::join);
    private static final Parser<Character> lp = ch('[').trim();
    private static final Parser<Character> rp = ch(']').trim();
    private static final Parser<Character> comma = ch(',').trim();
    private static final Parser<Object> listItem = oneOf(decimal, integer, string, lazy(() -> ListParser.list));
    private static final Parser<List<Object>> itemList = listItem.and(skip(comma).and(listItem).many())
        .map(r -> reduceList(r.getFirst(), r.getSecond()));
    private static final Parser<List<Object>> list = skip(lp).and(itemList.opt(Collections.emptyList())).skip(rp);
    private static final Parser<List<Object>> parser = list.end();

    private static String join(List<?> list) {
        return list.stream().map(Objects::toString).collect(Collectors.joining());
    }

    private static <T> List<T> reduceList(T first, List<T> remain) {
        List<T> list = new ArrayList<>();
        list.add(first);
        list.addAll(remain);
        return list;
    }

    public static List<Object> parse(String s) {
        return parser.parse(s);
    }
}
