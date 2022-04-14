package byx.parserc.interpreter;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {
    @Test
    public void test() {
        assertEquals(Map.of("i", 123), Interpreter.run("var i = 123"));
        assertEquals(Map.of("i", 10), Interpreter.run("var i = (2+3) * 4 / (9-7)"));
        assertEquals(Map.of("i", 17), Interpreter.run("var i = 2 + 3*5"));
        assertEquals(Map.of("i", 101), Interpreter.run("var i = 100 i = i + 1"));
        assertEquals(Map.of("i", 456), Interpreter.run("var i = 123\ni = 456"));
        assertEquals(Map.of("i", 3, "j", 124), Interpreter.run("var i = 100 + 2*3 i = 123 var j = i + 1 {var k = 3 i = k}"));
        assertEquals(Map.of("i", 123, "j", 4567), Interpreter.run("var i = 123 var j = 4567"));
        assertEquals(Map.of("i", 9134, "j", 4567), Interpreter.run("var i = 123 var j = 4567 i = j * 2"));
        assertEquals(Map.of("i", 123), Interpreter.run("var i = 123 if (i > 200) i = 456"));
        assertEquals(Map.of("i", 456), Interpreter.run("var i = 123 if (200 > i) i = 456"));
        assertEquals(Map.of("i", 789), Interpreter.run("var i = 123 if (i > 200) i = 456 else i = 789"));
        assertEquals(Map.of("i", 200), Interpreter.run("var i = 100 if (i >= 25 || !(i < 50)) i = 200"));
        assertEquals(Map.of("i", 1001, "j", 1002), Interpreter.run("var i = 123 var j = 456 if (i < 200 && j > 300) {i = 1001 j = 1002} else {i = 1003 j = 1004}"));
        assertEquals(Map.of("i", 1003, "j", 1004), Interpreter.run("var i = 123 var j = 456 if (i > 200 && j > 300) {\ni = 1001 j = 1002\t\n} else {\ni = 1003 j = 1004\t\n}"));
        assertEquals(Map.of("s", 5050), Interpreter.run("var s = 0 for (var i = 1; i <= 100; i = i + 1) s = s + i"));
        assertEquals(Map.of("s1", 2550, "s2", 2500), Interpreter.run("var s1 = 0 var s2 = 0 for (var i = 1; i <= 100; i = i + 1) {if (i % 2 == 0) s1 = s1 + i else s2 = s2 + i}"));
        assertEquals(Map.of("s", 5050, "i", 101), Interpreter.run("var s = 0 var i = 1 while (i <= 100) {s = s + i i = i + 1}"));
        assertEquals(Map.of("s1", 2550, "s2", 2500, "i", 101), Interpreter.run("var s1 = 0 var s2 = 0 var i = 1 while (i <= 100) {if (i % 2 == 0) s1 = s1 + i else s2 = s2 + i i = i + 1}"));
        assertEquals(Map.of("s", 349866), Interpreter.run("var s = 0 for (var i = 0; i < 10000; i = i + 1) {if (i % 3242 == 837) break s = s + i}"));
        assertEquals(Map.of("s", 349866, "i", 837), Interpreter.run("var s = 0 var i = 0 while (i < 10000) {if (i % 3242 == 837) break s = s + i i = i + 1}"));
        assertEquals(Map.of("s", 277694), Interpreter.run("var s = 0 for (var i = 0; i < 100; i = i + 1){if (i % 6 == 4)continue s = s + i * i}"));
        assertEquals(Map.of("s", 277694, "i", 100), Interpreter.run("var s = 0 var i = 0 while (i < 100){if (i % 6 == 4){i = i + 1 continue} s = s + i * i i = i + 1}"));
    }
}
