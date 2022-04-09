package byx.parserc;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static byx.parserc.Parsers.*;

public class JsonParser {
    private static final Parser<Character> whitespace = oneOf(' ', '\t', '\n', '\r');
    private static final Parser<String> digit = range('0', '9').map(Objects::toString);
    private static final Parser<Integer> integer = digit.many1()
            .map(nums -> Integer.parseInt(join(nums)))
            .transform(JsonParser::withWhitespace);
    private static final Parser<Double> decimal = seq(
            digit.many1(),
            ch('.'),
            digit.many1(),
            (a, b, c) -> Double.parseDouble(join(a) + b + join(c))
    ).transform(JsonParser::withWhitespace);
    private static final Parser<String> string = seq(
            ch('"'),
            not('"').many(),
            ch('"'),
            (a, b, c) -> join(b)
    ).transform(JsonParser::withWhitespace);
    private static final Parser<Boolean> bool = string("true").or(string("false"))
            .map(Boolean::parseBoolean)
            .transform(JsonParser::withWhitespace);
    private static final Parser<Character> objStart = withWhitespace(ch('{'));
    private static final Parser<Character> objEnd = withWhitespace(ch('}'));
    private static final Parser<Character> arrStart = withWhitespace(ch('['));
    private static final Parser<Character> arrEnd = withWhitespace(ch(']'));
    private static final Parser<Character> colon = withWhitespace(ch(':'));
    private static final Parser<Character> comma = withWhitespace(ch(','));
    private static final Parser<List<Object>> emptyArr = seq(arrStart, arrEnd, (a, b) -> Collections.emptyList());
    private static final Parser<List<Object>> arr = seq(
            arrStart,
            separateBy(comma, lazy(JsonParser::getJsonObj)).ignoreDelimiter(),
            arrEnd,
            (a, b, c) -> b
    ).or(emptyArr);
    private static final Parser<Pair<String, Object>> pair = seq(
            string,
            colon,
            lazy(JsonParser::getJsonObj),
            (a, b, c) -> new Pair<>(a, c)
    );
    private static final Parser<Map<String, Object>> emptyObj = seq(objStart, objEnd, (a, b) -> Collections.emptyMap());
    private static final Parser<Map<String, Object>> obj = seq(
            objStart,
            separateBy(comma, pair).ignoreDelimiter(),
            objEnd,
            (a, b, c) -> b.stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
    ).or(emptyObj);
    private static final Parser<Object> jsonObj =
            decimal.mapTo(Object.class)
            .or(integer.mapTo(Object.class))
            .or(string.mapTo(Object.class))
            .or(bool.mapTo(Object.class))
            .or(arr.mapTo(Object.class))
            .or(obj.mapTo(Object.class));

    private static <T> Parser<T> withWhitespace(Parser<T> p) {
        return skip(whitespace.many()).and(p).skip(whitespace.many());
    }
    
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
