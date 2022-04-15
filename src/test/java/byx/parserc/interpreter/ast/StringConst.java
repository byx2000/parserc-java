package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Environment;
import byx.parserc.interpreter.runtime.Value;

public class StringConst implements Expr {
    private final String value;

    public StringConst(String value) {
        this.value = value;
    }

    @Override
    public Value eval(Environment env) {
        return Value.of(value);
    }
}
