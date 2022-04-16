package byx.parserc.interpreter;

import byx.parserc.interpreter.runtime.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {
    private void verifyValue(Object expected, Value actual) {
        if (actual instanceof IntegerValue) {
            assertEquals(expected, ((IntegerValue) actual).getValue());
        } else if (actual instanceof DoubleValue) {
            assertEquals(expected, ((DoubleValue) actual).getValue());
        } else if (actual instanceof StringValue) {
            assertEquals(expected, ((StringValue) actual).getValue());
        } else if (actual instanceof BoolValue) {
            assertEquals(expected, ((BoolValue) actual).getValue());
        } else if (actual instanceof ListValue) {
            @SuppressWarnings("unchecked")
            List<Object> expectedList = (List<Object>) expected;
            List<Value> actualList = ((ListValue) actual).getValue();
            assertEquals(expectedList.size(), actualList.size());
            for (int i = 0; i < expectedList.size(); ++i) {
                verifyValue(expectedList.get(i), actualList.get(i));
            }
        } else if (actual instanceof ObjectValue) {
            @SuppressWarnings("unchecked")
            Map<String, Object> expectedProps = (Map<String, Object>) expected;
            Map<String, Value> actualProps = ((ObjectValue) actual).getProps();
            expectedProps.forEach((k, v) -> verifyValue(v, actualProps.get(k)));
        } else {
            fail();
        }
    }

    private void verify(String program, Map<String, Object> expected) {
        Map<String, Value> result = Interpreter.run(program);
        expected.forEach((k, v) -> verifyValue(v, result.get(k)));
    }

    @Test
    public void test() {
        // 变量定义
        assertEquals(Map.of("i", Value.of(123)), Interpreter.run("var i = 123"));
        assertEquals(Map.of("x", Value.of(12.34)), Interpreter.run("var x = 12.34;"));
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
        assertEquals(Map.of("i", Value.of(40.8)), Interpreter.run("var i = 12 * 3.4;"));
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
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = true || false;"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = false || true;"));
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = false || false"));

        // !
        assertEquals(Map.of("i", Value.of(false)), Interpreter.run("var i = !true"));
        assertEquals(Map.of("i", Value.of(true)), Interpreter.run("var i = !false"));

        assertEquals(Map.of("i", Value.of(10)), Interpreter.run("var i = (2+3) * 4 / (9-7)"));
        assertEquals(Map.of("i", Value.of(17)), Interpreter.run("var i = 2 + 3*5"));
        assertEquals(Map.of("i", Value.of(101)), Interpreter.run("var i = 100 i = i + 1"));
        assertEquals(Map.of("i", Value.of(456)), Interpreter.run("var i = 123\ni = 456"));
        assertEquals(Map.of("i", Value.of(3), "j", Value.of(124)), Interpreter.run("var i = 100 + 2*3; i = 123; var j = i + 1; {var k = 3 i = k}"));
        assertEquals(Map.of("i", Value.of(123), "j", Value.of(4567)), Interpreter.run("var i = 123 var j = 4567"));
        assertEquals(Map.of("i", Value.of(9134), "j", Value.of(4567)), Interpreter.run("var i = 123 var j = 4567 i = j * 2"));
        assertEquals(Map.of("i", Value.of(123)), Interpreter.run("var i = 123 if (i > 200) i = 456"));
        assertEquals(Map.of("i", Value.of(456)), Interpreter.run("var i = 123 if (200 > i) i = 456"));
        assertEquals(Map.of("i", Value.of(789)), Interpreter.run("var i = 123 if (i > 200) i = 456 else i = 789"));
        assertEquals(Map.of("i", Value.of(200)), Interpreter.run("var i = 100 if (i >= 25 || !(i < 50)) i = 200"));
        assertEquals(Map.of("i", Value.of(1001), "j", Value.of(1002)), Interpreter.run("var i = 123; var j = 456; if (i < 200 && j > 300) {i = 1001; j = 1002;} else {i = 1003; j = 1004;}"));
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

        assertEquals(Map.of("x", Value.of(56088)), Interpreter.run("var fun = (a, b) => 123 * 456 var x = fun()"));
        assertEquals(Map.of("x", Value.of(102)), Interpreter.run("var x = 100 var fun = () => {x = x + 1} fun() fun()"));
        assertEquals(Map.of("x", Value.of(101)), Interpreter.run("var x = 100; (() => {x = x + 1})()"));
        assertEquals(Map.of("x", Value.of(12345)), Interpreter.run("var x = (() => 12345)()"));
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
        assertEquals(Map.of("s", Value.of(10)), Interpreter.run("var observer = callback => {for (var i = 1; i <= 10; i = i + 1) callback(i)} var s = 0 observer(() => {s = s + 1})"));
        assertEquals(Map.of("x", Value.of(55)), Interpreter.run("var fib = n => {if (n == 1 || n == 2) return 1 else return fib(n - 1) + fib(n - 2)} var x = fib(10)"));
        assertEquals(Map.of("x", Value.of(3628800)), Interpreter.run("var factories = n => {if (n == 1) return 1 else return n * factories(n - 1)} var x = factories(10)"));
        assertEquals(Map.of("x", Value.of(300)), Interpreter.run("var func = (a, b) => a + b var x = func(100, 200, 400)"));
        assertThrows(InterpretException.class, () -> Interpreter.run("var func = (a, b, c) => a + b + c var x = func(100, 200)"));

        assertEquals(Map.of(
                "obj", Value.of(Map.of("a", Value.of(123), "b", Value.of(3.14), "c", Value.of("hello"), "d", Value.of(Map.of("x", Value.of(100), "y", Value.of(200.1))))),
                "x", Value.of(123),
                "y", Value.of(3.14),
                "z", Value.of("hello"),
                "w", Value.of(Map.of("x", Value.of(100), "y", Value.of(200.1))),
                "p", Value.of(25)
        ), Interpreter.run("var obj = {a: 123, b: 3.14, c: 'hello', d: {x: 100, y: 200.1}, method: (a, b) => a + b} var x = obj.a var y = obj.b var z = obj.c var w = obj.d var p = obj.method(12, 13)"));

        Map<String, Value> expect = new HashMap<>();
        expect.put("a", Value.of(100));
        expect.put("b", Value.of(101));
        expect.put("c", Value.of(102));
        expect.put("d", Value.of(101));
        expect.put("e", Value.of(100));
        expect.put("x", Value.of(200));
        expect.put("y", Value.of(201));
        expect.put("z", Value.of(202));
        expect.put("m", Value.of(201));
        expect.put("p", Value.of(200));
        expect.put("c1", Value.of(Map.of()));
        expect.put("c2", Value.of(Map.of()));
        assertEquals(expect, Interpreter.run(
                "var counter = init => {\n" +
                "    var cnt = init\n" +
                "    return {\n" +
                "        // 获取当前计数值\n" +
                "        current: () => cnt,\n" +
                "        // 计数值+1\n" +
                "        inc: () => {cnt = cnt + 1},\n" +
                "        // 计数值-1\n" +
                "        dec: () => {cnt = cnt - 1}\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "var c1 = counter(100)\n" +
                "var a = c1.current() // 100\n" +
                "c1.inc()\n" +
                "var b = c1.current() // 101\n" +
                "c1.inc()\n" +
                "var c = c1.current() // 102\n" +
                "c1.dec()\n" +
                "var d = c1.current() // 101\n" +
                "c1.dec()\n" +
                "var e = c1.current() // 100\n" +
                "\n" +
                "var c2 = counter(200)\n" +
                "// 200\n" +
                "var x = c2.current() \n" +
                "c2.inc()\n" +
                "// 201\n" +
                "var y = c2.current()\n" +
                "c2.inc()\n" +
                "// 202\n" +
                "var z = c2.current()\n" +
                "c2.dec()\n" +
                "// 201\n" +
                "var m = c2.current()\n" +
                "c2.dec()\n" +
                "// 200\n" +
                "var p = c2.current()"));

        expect = new HashMap<>();
        expect.put("s1Name", Value.of("Xiao Ming"));
        expect.put("s1Age", Value.of(21));
        expect.put("s1Score", Value.of(87.5));
        expect.put("s2Name", Value.of("Li Si"));
        expect.put("s2Age", Value.of(23));
        expect.put("s2Score", Value.of(95));
        expect.put("s1", Value.of(Map.of("score", Value.of(87.5))));
        expect.put("s2", Value.of(Map.of("score", Value.of(95))));
        assertEquals(expect, Interpreter.run(
                "var Student = (name, age, score) => {\n" +
                "    return {\n" +
                "        getName: () => name,\n" +
                "        getAge: () => age,\n" +
                "        score: score,\n" +
                "        setName: _name => {name = _name},\n" +
                "        setAge: _age => {age = _age},\n" +
                "        setScore: _score => {score = _score}\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "var s1 = Student('Zhang San', 21, 87.5)\n" +
                "var s2 = Student('Li Si', 23, 95)\n" +
                "s1.setName('Xiao Ming')\n" +
                "s2.setScore(77.5)\n" +
                "\n" +
                "var s1Name = s1.getName()\n" +
                "var s1Age = s1.getAge()\n" +
                "var s1Score = s1.score\n" +
                "var s2Name = s2.getName()\n" +
                "var s2Age = s2.getAge()\n" +
                "var s2Score = s2.score"));

        // 数组
        verify("var arr = [123, 3.14, [true, 3], 'hello', []] var a = arr[0] var b = arr[1] var c = arr[2] var d = arr[3] var e = arr[4]", Map.of(
                "arr", List.of(123, 3.14, List.of(true, 3), "hello", List.of()),
                "a", 123,
                "b", 3.14,
                "c", List.of(true, 3),
                "d", "hello",
                "e", List.of()
        ));
        verify("var arr = [[1, 2, 3], [4, 5, 6]] var a = arr[0][0] var b = arr[0][1] var c = arr[1][2] var d = arr[0] var e = arr[1]", Map.of(
                "a", 1,
                "b", 2,
                "c", 6,
                "d", List.of(1, 2, 3),
                "e", List.of(4, 5, 6)
        ));
        verify("var arr = [(a, b) => a + b, (a, b) => a - b] var x = arr[0](3, 5) var y = arr[1](100, 40)", Map.of(
                "x", 8,
                "y", 60
        ));
        verify("var a = [{name: 'Zhang San', age: 17, scores: [100, 90, 80]}, {name: 'Li Si', age: 21, scores: [75, 80, 81]}] var s1 = a[0] var x = a[1].name var y = a[0].scores[1]", Map.of(
                "s1", Map.of("name", "Zhang San", "age", 17, "scores", List.of(100, 90, 80)),
                "x", "Li Si",
                "y", 90
        ));
        verify("var arr = [1, 2, 3] var len1 = arr.length() arr.add(4) arr.add(5) var len2 = arr.length() arr.add(3.14) arr.add('hello') var len3 = arr.length()", Map.of(
                "arr", List.of(1, 2, 3, 4, 5, 3.14, "hello"),
                "len1", 3,
                "len2", 5,
                "len3", 7
        ));
        verify("var nums = [1, 2, 3, 4, 5] var s = 0 for (var i = 0; i < nums.length(); i = i + 1) s = s + nums[i]", Map.of(
                "s", 15
        ));
        verify("var nums = [] for (var i = 1; i <= 100; i = i + 1) nums.add(i * i) var s = 0 for (var i = 0; i < nums.length(); i = i + 1) s = s + nums[i]", Map.of(
            "s", 338350
        ));
        verify("var arr = [1, 2, 3, 4] var len1 = arr.length() var rm = arr.remove(2) var len2 = arr.length()", Map.of(
                "len1", 4,
                "rm", 3,
                "len2", 3,
                "arr", List.of(1, 2, 4)
        ));

        // string内建方法
        assertEquals(Map.of("len", Value.of(5)), Interpreter.run("var len = 'hello'.length()"));
        assertEquals(Map.of("sub", Value.of("ell")), Interpreter.run("var sub = 'hello'.substring(1, 4)"));
        assertEquals(Map.of("s1", Value.of("hello world!"), "s2", Value.of("hello world! aaa bbb")), Interpreter.run("var s1 = 'hello'.concat(' world!') var s2 = s1.concat(' aaa').concat(' bbb')"));
        assertEquals(Map.of("i", Value.of(123), "d", Value.of(12.56), "b", Value.of(true)), Interpreter.run("var i = '123'.toInt() var d = '12.56'.toDouble() var b = 'true'.toBool()"));
    }
}
