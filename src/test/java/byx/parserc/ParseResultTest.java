package byx.parserc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParseResultTest {
    @Test
    public void test() {
        Cursor remain = new Cursor("hello", 3);
        ParseResult<String> r = new ParseResult<>(remain, "aaa");
        assertSame(remain, r.getRemain());
        assertEquals("aaa", r.getResult());
        assertEquals("ParseResult{result=aaa, remain=lo}", r.toString());
    }
}
