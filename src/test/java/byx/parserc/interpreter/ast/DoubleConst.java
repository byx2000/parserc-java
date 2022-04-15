package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Environment;
import byx.parserc.interpreter.runtime.Value;

public class DoubleConst implements Expr {
    private final double value;

    public DoubleConst(double value) {
        this.value = value;
    }

    @Override
    public Value eval(Environment env) {
        return Value.of(value);
    }
}
