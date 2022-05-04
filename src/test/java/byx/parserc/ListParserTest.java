package byx.parserc;

import byx.parserc.exception.ParseException;
import org.junit.jupiter.api.Test;

import java.util.List;

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
