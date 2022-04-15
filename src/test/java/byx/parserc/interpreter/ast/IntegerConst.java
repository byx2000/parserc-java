package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Environment;
import byx.parserc.interpreter.runtime.Value;

public class IntegerConst implements Expr {
    private final int value;

    public IntegerConst(int value) {
        this.value = value;
    }

    @Override
    public Value eval(Environment env) {
        return Value.of(value);
    }
}
