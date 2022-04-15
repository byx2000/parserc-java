package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Scope;
import byx.parserc.interpreter.runtime.Value;

public class DoubleConst implements Expr {
    private final double value;

    public DoubleConst(double value) {
        this.value = value;
    }

    @Override
    public Value eval(Scope scope) {
        return Value.of(value);
    }
}
