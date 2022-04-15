package byx.parserc.interpreter;

import byx.parserc.interpreter.runtime.Value;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {
    @Test
    public void test() {
        // 变量定义
        assertEquals(Map.of("i", Value.of(123)), Interpreter.run("var i = 123"));
        assertEquals(Map.of("x", Value.of(12.34)), Interpreter.run("var x = 12.34"));
        assertEquals(Map.of("s", Value.of("hello")), Interpreter.run("var s = 'hello'"));
        assertEquals(Map.of("b1", Value.of(true), "b2", Value.of(false)), Interpreter.run("var b1 = true var b2 = false"));

        // +
        assertEquals(Map.of("i", Value.of(126.14)), Interpreter.run("var i = 123 + 3.14"));
        assertEquals(Map.of("i", Value.of(567.34)), Interpreter.run("var i = 12.34 + 555"));
        assertEquals(Map.of("i", Value.of(69.12)), Interpreter.run("var i = 12.34 + 56.78"));
        assertEquals(Map.of("i", Value.of("hello world!")), Interpreter.run("var i = 'hello ' + 'world!'"));
        assertEquals(Map.of("i", Value.of("123 hello")), Interpreter.run("var i = 123 + ' hello'"));
        assertEquals(Map.of("i", Value.of("hello 123")), Interpreter.run("var i = 'hello ' + 123"));
        assertEquals(Map.of("i", Value.of("3.14 hello")), Interpreter.run("var i = 3.14 + ' hello'"));
        assertEquals(Map.of("i", Value.of("hello 3.14")), Interpreter.run("var i = 'hello ' + 3.14"));
        assertEquals(Map.of("i", Value.of("abctrue")), Interpreter.run("var i = 'abc' + true"));
        assertEquals(Map.of("i", Value.of("falseabc")), Interpreter.run("var i = false + 'abc'"));

        // -
        assertEquals(Map.of("i", Value.of(498)), Interpreter.run("var i = 532 - 34"));
        assertEquals(Map.of("i", Value.of(-8.86)), Interpreter.run("var i = 3.14 - 12"));
        assertEquals(Map.of("i", Value.of(4.22)), Interpreter.run("var i = 12 - 7.78"));
        assertEquals(Map.of("i", Value.of(44.44)), Interpreter.run("var i = 56.78 - 12.34"));

        // *
        assertEquals(Map.of("i", Value.of(408)), Interpreter.run("var i = 12 * 34"));
        assertEquals(Map.of("i", Value.of(40.8)), Interpreter.run("var i = 12 * 3.4"));
        assertEquals(Map.of("i", Value.of(4.08)), Interpreter.run("var i = 0.12 * 34"));
        assertEquals(Map.of("i", Value.of(700.6652)), Interpreter.run("var i = 12.34 * 56.78"));

        // /
        assertEquals(Map.of("i", Value.of(2)), Interpreter.run("var i = 5 / 2"));
        assertEquals(Map.of("i", Value.of(12 / 3.4)), Interpreter.run("var i = 12 / 3.4"));
        assertEquals(Map.of("i", Value.of(0.12 / 34)), Interpreter.run("var i = 0.12 / 34"));
        assertEquals(Map.of("i", Value.of(56.78 / 12.34)), Interpreter.run("var i = 56.78 / 12.34"));

        // %
        assertEquals(Map.of("i", Value.of(1)), Interpreter.run("var i = 5 % 2"));
        assertEquals(Map.of("i", Value.of(3)), Interpreter.run("var i = 3 % 12"));

        // >
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = 100 > 50"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = 3.14 > 50"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = 3.14 > 1"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = 3.14 > 456.23"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = 'banana' > 'apple'"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = 'apple' > 'banana'"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = 'apple' > 'apple'"));

        // >=
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = 100 >= 50"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = 3.14 >= 50"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = 3.14 >= 1"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = 3.14 >= 456.23"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = 'banana' >= 'apple'"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = 'apple' >= 'banana'"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = 'apple' >= 'apple'"));

        // <
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = 100 < 50"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = 3.14 < 50"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = 3.14 < 1"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = 3.14 < 456.23"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = 'banana' < 'apple'"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = 'apple' < 'banana'"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = 'apple' < 'apple'"));

        // <=
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = 100 <= 50"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = 3.14 <= 50"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = 3.14 <= 1"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = 3.14 <= 456.23"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = 'banana' <= 'apple'"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = 'apple' <= 'banana'"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = 'apple' <= 'apple'"));

        // ==
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = 123 == 123"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = 12.34 == 12.34"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = true == true"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = false == false"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = 'hello' == 'hello'"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = 123 == 321"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = 12.34 == 56.78"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = true == false"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = 'hello' == 'world'"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = 123 == 3.14"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = 3.14 == 321"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = false == 567"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = 123 == 'hello'"));

        // !=
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = 123 != 123"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = 12.34 != 12.34"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = true != true"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = false != false"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = 'hello' != 'hello'"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = 123 != 321"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = 12.34 != 56.78"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = true != false"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = 'hello' != 'world'"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = 123 != 3.14"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = 3.14 != 321"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = false != 567"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = 123 != 'hello'"));

        // &&
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = true && true"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = true && false"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = false && true"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = false && false"));

        // ||
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = true || true"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = true || false"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = false || true"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = false || false"));

        // !
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = !true"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = !false"));

        assertEquals(Map.of("i", Value.of(10)), Interpreter.run("var i = (2+3) * 4 / (9-7)"));
        assertEquals(Map.of("i", Value.of(17)), Interpreter.run("var i = 2 + 3*5"));
        assertEquals(Map.of("i", Value.of(101)), Interpreter.run("var i = 100 i = i + 1"));
        assertEquals(Map.of("i", Value.of(456)), Interpreter.run("var i = 123\ni = 456"));
        assertEquals(Map.of("i", Value.of(3), "j", Value.of(124)), Interpreter.run("var i = 100 + 2*3 i = 123 var j = i + 1 {var k = 3 i = k}"));
        assertEquals(Map.of("i", Value.of(123), "j", Value.of(4567)), Interpreter.run("var i = 123 var j = 4567"));
        assertEquals(Map.of("i", Value.of(9134), "j", Value.of(4567)), Interpreter.run("var i = 123 var j = 4567 i = j * 2"));
        assertEquals(Map.of("i", Value.of(123)), Interpreter.run("var i = 123 if (i > 200) i = 456"));
        assertEquals(Map.of("i", Value.of(456)), Interpreter.run("var i = 123 if (200 > i) i = 456"));
        assertEquals(Map.of("i", Value.of(789)), Interpreter.run("var i = 123 if (i > 200) i = 456 else i = 789"));
        assertEquals(Map.of("i", Value.of(200)), Interpreter.run("var i = 100 if (i >= 25 || !(i < 50)) i = 200"));
        assertEquals(Map.of("i", Value.of(1001), "j", Value.of(1002)), Interpreter.run("var i = 123 var j = 456 if (i < 200 && j > 300) {i = 1001 j = 1002} else {i = 1003 j = 1004}"));
        assertEquals(Map.of("i", Value.of(1003), "j", Value.of(1004)), Interpreter.run("var i = 123 var j = 456 if (i > 200 && j > 300) {\ni = 1001 j = 1002\t\n} else {\ni = 1003 j = 1004\t\n}"));
        assertEquals(Map.of("s", Value.of(5050)), Interpreter.run("var s = 0 for (var i = 1; i <= 100; i = i + 1) s = s + i"));
        assertEquals(Map.of("s1", Value.of(2550), "s2", Value.of(2500)), Interpreter.run("var s1 = 0 var s2 = 0 for (var i = 1; i <= 100; i = i + 1) {if (i % 2 == 0) s1 = s1 + i else s2 = s2 + i}"));
        assertEquals(Map.of("s", Value.of(5050), "i", Value.of(101)), Interpreter.run("var s = 0 var i = 1 while (i <= 100) {s = s + i i = i + 1}"));
        assertEquals(Map.of("s1", Value.of(2550), "s2", Value.of(2500), "i", Value.of(101)), Interpreter.run("var s1 = 0 var s2 = 0 var i = 1 while (i <= 100) {if (i % 2 == 0) s1 = s1 + i else s2 = s2 + i i = i + 1}"));
        assertEquals(Map.of("s", Value.of(349866)), Interpreter.run("var s = 0 for (var i = 0; i < 10000; i = i + 1) {if (i % 3242 == 837) break s = s + i}"));
        assertEquals(Map.of("s", Value.of(349866), "i", Value.of(837)), Interpreter.run("var s = 0 var i = 0 while (i < 10000) {if (i % 3242 == 837) break s = s + i i = i + 1}"));
        assertEquals(Map.of("s", Value.of(277694)), Interpreter.run("var s = 0 for (var i = 0; i < 100; i = i + 1){if (i % 6 == 4)continue s = s + i * i}"));
        assertEquals(Map.of("s", Value.of(277694), "i", Value.of(100)), Interpreter.run("var s = 0 var i = 0 while (i < 100){if (i % 6 == 4){i = i + 1 continue} s = s + i * i i = i + 1}"));
        assertEquals(Map.of("s", Value.of(29441)), Interpreter.run("var s = 0 for (var i = 0; i < 1000; i = i + 1) if (i % 6 == 1 && (i % 7 == 2 || i % 8 == 3)) s = s + i"));
        assertEquals(Map.of("s", Value.of(71357)), Interpreter.run("var s = 0 for (var i = 0; i < 1000; i = i + 1) if (i % 6 == 1 && i % 7 == 2 || i % 8 == 3) s = s + i"));
        assertEquals(Map.of("s", Value.of("1 2 3 4 5 6 7 8 9 10")), Interpreter.run("var s = '' for (var i = 1; i <= 10; i = i + 1) {if (i != 10) s = s + i + ' ' else s = s + i}"));
        assertEquals(Map.of("s", Value.of("hello".repeat(100))), Interpreter.run("var s = '' for (var i = 0; i < 100; i = i + 1) s = s + 'hello'"));
        assertEquals(Map.of("s", Value.of(5.187377517639621)), Interpreter.run("var s = 0.0 for (var i = 1; i <= 100; i = i + 1) s = s + 1.0 / i"));

        assertEquals(Map.of("x", Value.of(102)), Interpreter.run("var x = 100 var fun = () => {x = x + 1} fun() fun()"));
        assertEquals(Map.of("x", Value.of(5), "y", Value.of(112)), Interpreter.run("var add = (a) => (b) => a + b var x = add(2)(3) var y = add(45)(67)"));
        assertEquals(Map.of("x", Value.of(5), "y", Value.of(112)), Interpreter.run("var add = a => b => a + b var x = add(2)(3) var y = add(45)(67)"));
        assertEquals(Map.of(
                "a", Value.of(1),
                "b", Value.of(2),
                "c", Value.of(3),
                "d", Value.of(1),
                "e", Value.of(2),
                "f", Value.of(3),
                "x", Value.of(4),
                "y", Value.of(4)
        ), Interpreter.run("var counter = () => {var cnt = 0 return () => {cnt = cnt + 1 return cnt}} var c1 = counter() var a = c1() var b = c1() var c = c1() var c2 = counter() var d = c2() var e = c2() var f = c2() var x = c1() var y = c2()"));
        assertEquals(Map.of("i", Value.of(201)), Interpreter.run("var compose = (n, f, g) => g(f(n)) var f1 = n => n * 2 var f2 = n => n + 1 var i = compose(100, f1, f2)"));
        assertEquals(Map.of("x", Value.of(123), "y", Value.of(456)), Interpreter.run("var x = 123 var outer = () => {var x = 456 return () => x} var y = outer()()"));
        assertEquals(Map.of("x", Value.of(100), "y", Value.of(456)), Interpreter.run("var x = 123 var outer = () => {var x = 456 return () => x} x = 100 var y = outer()()"));
        assertEquals(Map.of("s", Value.of(55)), Interpreter.run("var observer = callback => {for (var i = 1; i <= 10; i = i + 1) callback(i)} var s = 0 observer(n => {s = s + n})"));
        assertEquals(Map.of("x", Value.of(55)), Interpreter.run("var fib = n => {if (n == 1 || n == 2) return 1 else return fib(n - 1) + fib(n - 2)} var x = fib(10)"));
        assertEquals(Map.of("x", Value.of(3628800)), Interpreter.run("var factories = n => {if (n == 1) return 1 else return n * factories(n - 1)} var x = factories(10)"));
    }
}
