package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Environment;

public class Constant implements ArithmeticExpr {
    private final int value;

    public Constant(int value) {
        this.value = value;
    }

    @Override
    public int eval(Environment env) {
        return value;
    }
}
