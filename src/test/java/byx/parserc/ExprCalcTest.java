package byx.parserc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExprCalcTest {
    @Test
    public void test() throws ParseException {
        assertEquals(1.0, ExprCalc.eval("1"));
        assertEquals(1.0, ExprCalc.eval(" 1"));
        assertEquals(1.0, ExprCalc.eval("1\t"));
        assertEquals(123.0, ExprCalc.eval("\n123"));
        assertEquals(123.0, ExprCalc.eval("123 "));
        assertEquals(3.14, ExprCalc.eval(" 3.14"));
        assertEquals(-1.0, ExprCalc.eval("-1"));
        assertEquals(-123.0, ExprCalc.eval("-123 "));
        assertEquals(-3.14, ExprCalc.eval("-3.14 "));
        assertEquals(2.0 + 3.0, ExprCalc.eval("2+3"));
        assertEquals(5.2 - 7.56, ExprCalc.eval("5.2-7.56"));
        assertEquals(123.456 * 67.89, ExprCalc.eval("123.456*67.89"));
        assertEquals(0.78 / 10.4, ExprCalc.eval(" 0.78 / 10.4 "));
        assertEquals((2.0 + 3) * (7 - 4.0), ExprCalc.eval("(2+3)*(7-4)"));
        assertEquals(2.4 / 5.774 * (6 / 3.57 + 6.37) - 2 * 7 / 5.2 + 5, ExprCalc.eval("2.4 / 5.774 * (6 / 3.57 + 6.37) - 2 * 7 / 5.2 + 5"));
        assertEquals(77.58 * (6 / 3.14 + 55.2234) - 2 * 6.1 / (1.0 + 2 / (4.0 - 3.8 * 5)), ExprCalc.eval("77.58* ( 6 / 3.14+55.2234 ) -2 * 6.1/ ( 1.0+2/ (4.0-3.8*5))  "));

        assertThrows(ParseException.class, () -> ExprCalc.eval(""));
        assertThrows(ParseException.class, () -> ExprCalc.eval("abc"));
        assertThrows(ParseException.class, () -> ExprCalc.eval("12.34.56"));
        assertThrows(ParseException.class, () -> ExprCalc.eval("2+"));
        assertThrows(ParseException.class, () -> ExprCalc.eval("-2+3-"));
        assertThrows(ParseException.class, () -> ExprCalc.eval("2.5+*3/5"));
        assertThrows(ParseException.class, () -> ExprCalc.eval("()"));
        assertThrows(ParseException.class, () -> ExprCalc.eval("("));
        assertThrows(ParseException.class, () -> ExprCalc.eval(")"));
        assertThrows(ParseException.class, () -> ExprCalc.eval("2*(4+(3/5)"));
    }
}
