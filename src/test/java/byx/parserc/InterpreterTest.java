package byx.parserc;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {
    @Test
    public void test() {
        assertEquals(Map.of("i", 123), Interpreter.interpret("var i = 123"));
        assertEquals(Map.of("i", 10), Interpreter.interpret("var i = (2+3) * 4 / (9-7)"));
        assertEquals(Map.of("i", 17), Interpreter.interpret("var i = 2 + 3*5"));
        assertEquals(Map.of("i", 101), Interpreter.interpret("var i = 100 i = i + 1"));
        assertEquals(Map.of("i", 456), Interpreter.interpret("var i = 123\ni = 456"));
        assertEquals(Map.of("i", 123, "j", 4567), Interpreter.interpret("var i = 123 var j = 4567"));
        assertEquals(Map.of("i", 9134, "j", 4567), Interpreter.interpret("var i = 123 var j = 4567 i = j * 2"));

        assertEquals(Map.of("i", 123), Interpreter.interpret("var i = 123 if (i > 200) i = 456"));
        assertEquals(Map.of("i", 456), Interpreter.interpret("var i = 123 if (200 > i) i = 456"));
        assertEquals(Map.of("i", 789), Interpreter.interpret("var i = 123 if (i > 200) i = 456 else i = 789"));
        assertEquals(Map.of("i", 1001, "j", 1002), Interpreter.interpret("var i = 123 var j = 456 if (i < 200 && j > 300) {i = 1001 j = 1002} else {i = 1003 j = 1004}"));
        assertEquals(Map.of("i", 1003, "j", 1004), Interpreter.interpret("var i = 123 var j = 456 if (i > 200 && j > 300) {\ni = 1001 j = 1002\t\n} else {\ni = 1003 j = 1004\t\n}"));

        assertEquals(Map.of("s", 5050), Interpreter.interpret("var s = 0 for (var i = 1; i <= 100; i = i + 1) s = s + i"));
        assertEquals(Map.of("s1", 2550, "s2", 2500), Interpreter.interpret("var s1 = 0 var s2 = 0 for (var i = 1; i <= 100; i = i + 1) {if (i % 2 == 0) s1 = s1 + i else s2 = s2 + i}"));

        assertEquals(Map.of("i", 100), Interpreter.interpret("var i = 100 {var i = 200 i = 300}"));
        assertEquals(Map.of("i", 200), Interpreter.interpret("var i = 100 {var j = 200 i = j}"));
    }
}
