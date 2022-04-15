package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Scope;
import byx.parserc.interpreter.runtime.Value;

public class StringConst implements Expr {
    private final String value;

    public StringConst(String value) {
        this.value = value;
    }

    @Override
    public Value eval(Scope scope) {
        return Value.of(value);
    }
}
