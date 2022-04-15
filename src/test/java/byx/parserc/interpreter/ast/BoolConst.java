package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Environment;
import byx.parserc.interpreter.runtime.Value;

public class BoolConst implements Expr {
    private final boolean value;

    public BoolConst(boolean value) {
        this.value = value;
    }

    @Override
    public Value eval(Environment env) {
        return Value.of(value);
    }
}
