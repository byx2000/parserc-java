package byx.parserc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static byx.parserc.Parsers.*;

public class ValueTest {
    @Test
    public void test() throws ParseException {
        Parser<Integer, Character> p = value(123);
        ParseResult<Integer, Character> r = p.parse(Cursor.of("abc"));
        assertEquals(123, r.getResult());
        assertEquals("abc", r.getRemain().toString());

        r = p.parse(Cursor.of(""));
        assertEquals(123, r.getResult());
        assertEquals("", r.getRemain().toString());
    }
}
