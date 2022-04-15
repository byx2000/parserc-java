package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Scope;
import byx.parserc.interpreter.runtime.Value;

public class BoolConst implements Expr {
    private final boolean value;

    public BoolConst(boolean value) {
        this.value = value;
    }

    @Override
    public Value eval(Scope scope) {
        return Value.of(value);
    }
}
