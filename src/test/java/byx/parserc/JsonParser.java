package byx.parserc;

import byx.parserc.exception.ParseException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static byx.parserc.Parsers.*;

/**
 * json解析器
 */
public class JsonParser {
    private static final Parser<Character> w = chs(' ', '\t', '\n', '\r');
    private static final Parser<List<Character>> ws = w.many();
    private static final Parser<String> digit = range('0', '9').map(Objects::toString);
    private static final Parser<String> digits = digit.many1().map(JsonParser::join);
    private static final Parser<Integer> integer = digits.map(Integer::parseInt).surround(ws);
    private static final Parser<Double> decimal = seq(digits, ch('.'), digits).map(JsonParser::join).map(Double::parseDouble);
    private static final Parser<String> string = skip(ch('"')).and(not('"').many()).skip(ch('"')).map(JsonParser::join);
    private static final Parser<Boolean> bool = strs("true", "false").map(Boolean::parseBoolean).surround(ws);
    private static final Parser<Character> objStart = ch('{').surround(ws);
    private static final Parser<Character> objEnd = ch('}').surround(ws);
    private static final Parser<Character> arrStart = ch('[').surround(ws);
    private static final Parser<Character> arrEnd = ch(']').surround(ws);
    private static final Parser<Character> colon = ch(':').surround(ws);
    private static final Parser<Character> comma = ch(',').surround(ws);
    private static final Parser<Object> lazyJsonObj = lazy(JsonParser::getJsonObj);
    private static final Parser<List<Object>> arr = skip(arrStart).and(separate(comma, lazyJsonObj).opt(Collections.emptyList())).skip(arrEnd);
    private static final Parser<Pair<String, Object>> pair = string.skip(colon).and(lazyJsonObj);
    private static final Parser<Map<String, Object>> obj = skip(objStart).and(separate(comma, pair).opt(Collections.emptyList())).skip(objEnd)
            .map(ps -> ps.stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
    private static final Parser<Object> jsonObj = oneOf(
            decimal.mapTo(Object.class),
            integer.mapTo(Object.class),
            string.mapTo(Object.class),
            bool.mapTo(Object.class),
            arr.mapTo(Object.class),
            obj.mapTo(Object.class)
    );
    
    private static Parser<Object> getJsonObj() {
        return jsonObj;
    }

    private static String join(List<?> list) {
        return list.stream().map(Objects::toString).collect(Collectors.joining(""));
    }

    public static Object parse(String input) throws ParseException {
        return jsonObj.parse(input);
    }
}
