package byx.parserc;

import byx.parserc.exception.ParseException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JsonParserTest {
    @Test
    public void test() throws ParseException {
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

        String json = "{\n" +
                "    \"a\": 123,\n" +
                "    \"b\": 3.14,\n" +
                "    \"c\": \"hello\",\n" +
                "    \"d\": {\n" +
                "        \"x\": 100,\n" +
                "        \"y\": \"world!\"\n" +
                "    },\n" +
                "    \"e\": [\n" +
                "        12,\n" +
                "        34.56,\n" +
                "        {\n" +
                "            \"name\": \"Xiao Ming\",\n" +
                "            \"age\": 18,\n" +
                "            \"score\": [99.8, 87.5, 60.0]\n" +
                "        },\n" +
                "        \"abc\"\n" +
                "    ],\n" +
                "    \"f\": [],\n" +
                "    \"g\": {},\n" +
                "    \"h\": [true, {\"m\": false}]\n" +
                "}";
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

        assertThrows(ParseException.class, () -> JsonParser.parse(""));
        assertThrows(ParseException.class, () -> JsonParser.parse("{"));
        assertThrows(ParseException.class, () -> JsonParser.parse("{}}"));
        assertThrows(ParseException.class, () -> JsonParser.parse("[{]}"));
        assertThrows(ParseException.class, () -> JsonParser.parse("[100 200 300]"));
        assertThrows(ParseException.class, () -> JsonParser.parse("[1,2,3],4"));
    }
}
