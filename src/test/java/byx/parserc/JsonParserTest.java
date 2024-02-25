package byx.parserc;

import byx.parserc.exception.ParseInternalException;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static byx.parserc.Parsers.*;
import static org.junit.jupiter.api.Assertions.*;

public class JsonParserTest {
    @Test
    public void test() throws ParseInternalException {
        assertEquals(123, JsonParser.parse("123"));
        assertEquals(3.14, JsonParser.parse("3.14"));
        assertEquals(true, JsonParser.parse("true"));
        assertEquals(false, JsonParser.parse("false"));
        assertEquals("hello!", JsonParser.parse("\"hello!\""));
        assertEquals(List.of(), JsonParser.parse("[]"));
        assertEquals(List.of(), JsonParser.parse("[ ]"));
        assertEquals(Map.of(), JsonParser.parse("{}"));
        assertEquals(Map.of(), JsonParser.parse("{ }"));
        assertEquals(List.of(Map.of()), JsonParser.parse("[{}]"));

        String json = """
            {
                "a": 123,
                "b": 3.14,
                "c": "hello",
                "d": {
                    "x": 100,
                    "y": "world!"
                },
                "e": [
                    12,
                    34.56,
                    {
                        "name": "Xiao Ming",
                        "age": 18,
                        "score": [99.8, 87.5, 60.0]
                    },
                    "abc"
                ],
                "f": [],
                "g": {},
                "h": [true, {"m": false}]
            }""";
        Map<Object, Object> map = Map.of(
                "a", 123,
                "b", 3.14,
                "c", "hello",
                "d", Map.of(
                        "x", 100,
                        "y", "world!"
                ),
                "e", List.of(
                        12,
                        34.56,
                        Map.of(
                                "name", "Xiao Ming",
                                "age", 18,
                                "score", List.of(99.8, 87.5, 60.0)
                        ),
                        "abc"
                ),
                "f", List.of(),
                "g", Map.of(),
                "h", List.of(true, Map.of("m", false))
        );
        assertEquals(map, JsonParser.parse(json));

        assertThrows(ParseInternalException.class, () -> JsonParser.parse(""));
        assertThrows(ParseInternalException.class, () -> JsonParser.parse("{"));
        assertThrows(ParseInternalException.class, () -> JsonParser.parse("{}}"));
        assertThrows(ParseInternalException.class, () -> JsonParser.parse("[{]}"));
        assertThrows(ParseInternalException.class, () -> JsonParser.parse("[100 200 300]"));
        assertThrows(ParseInternalException.class, () -> JsonParser.parse("[1,2,3],4"));
    }
}

/**
 * json解析器
 */
class JsonParser {
    private static final Parser<String> digit = range('0', '9').map(Objects::toString);
    private static final Parser<String> digits = digit.many1().map(JsonParser::join);
    private static final Parser<Integer> integer = digits.map(Integer::parseInt).trim();
    private static final Parser<Double> decimal = seq(digits, ch('.'), digits).map(JsonParser::join).map(Double::parseDouble);
    private static final Parser<String> string = skip(ch('"')).and(not('"').many()).skip(ch('"')).map(JsonParser::join);
    private static final Parser<Boolean> bool = strs("true", "false").map(Boolean::parseBoolean).trim();
    private static final Parser<Character> objStart = ch('{').trim();
    private static final Parser<Character> objEnd = ch('}').trim();
    private static final Parser<Character> arrStart = ch('[').trim();
    private static final Parser<Character> arrEnd = ch(']').trim();
    private static final Parser<Character> colon = ch(':').trim();
    private static final Parser<Character> comma = ch(',').trim();
    private static final Parser<Object> lazyJsonObj = lazy(() -> JsonParser.jsonObj);
    private static final Parser<List<Object>> jsonObjList = lazyJsonObj.and(skip(comma).and(lazyJsonObj).many())
        .map(r -> reduceList(r.first(), r.second()));
    private static final Parser<List<Object>> arr = skip(arrStart).and(jsonObjList.opt(Collections.emptyList())).skip(arrEnd);
    private static final Parser<Pair<String, Object>> pair = string.skip(colon).and(lazyJsonObj);
    private static final Parser<List<Pair<String, Object>>> pairList = pair.and(skip(comma).and(pair).many())
        .map(r -> reduceList(r.first(), r.second()));
    private static final Parser<Map<String, Object>> obj = skip(objStart).and(pairList.opt(Collections.emptyList())).skip(objEnd)
        .map(ps -> ps.stream().collect(Collectors.toMap(Pair::first, Pair::second)));
    private static final Parser<Object> jsonObj = oneOf(decimal, integer, string, bool, arr, obj);
    private static final Parser<Object> parser = jsonObj.end();

    private static String join(List<?> list) {
        return list.stream().map(Objects::toString).collect(Collectors.joining(""));
    }

    private static <T> List<T> reduceList(T first, List<T> remain) {
        List<T> list = new ArrayList<>();
        list.add(first);
        list.addAll(remain);
        return list;
    }

    public static Object parse(String input) {
        return parser.parse(input);
    }
}
