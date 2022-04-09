package byx.parserc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoolExprCalcTest {
    @Test
    public void test() {
        assertTrue(BoolExprCalc.eval("t"));
        assertFalse(BoolExprCalc.eval("f"));
        assertFalse(BoolExprCalc.eval("!(t)"));
        assertTrue(BoolExprCalc.eval("!(f)"));
        assertTrue(BoolExprCalc.eval("&(t)"));
        assertFalse(BoolExprCalc.eval("&(f)"));
        assertTrue(BoolExprCalc.eval("|(t)"));
        assertFalse(BoolExprCalc.eval("|(f)"));
        assertTrue(BoolExprCalc.eval("&(t,t)"));
        assertFalse(BoolExprCalc.eval("&(t,f)"));
        assertFalse(BoolExprCalc.eval("&(f,t)"));
        assertFalse(BoolExprCalc.eval("&(f,f)"));
        assertTrue(BoolExprCalc.eval("|(t,t)"));
        assertTrue(BoolExprCalc.eval("|(t,f)"));
        assertTrue(BoolExprCalc.eval("|(f,t)"));
        assertFalse(BoolExprCalc.eval("|(f,f)"));
        assertFalse(BoolExprCalc.eval("|(&(t,f,t),!(t))"));
        assertTrue(BoolExprCalc.eval("!(&(!(t),&(f),|(f)))"));
    }
}
