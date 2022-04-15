package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.InterpretException;
import byx.parserc.interpreter.runtime.Value;

public class Rem extends BinaryOp {
    public Rem(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    @Override
    protected Value doEval(Value v1, Value v2) {
        if (v1.isInteger() && v2.isInteger()) {
            return Value.of(v1.getInteger() % v2.getInteger());
        }
        throw new InterpretException(String.format("Unsupported operator %% between %s and %s", v1.getValue(), v2.getValue()));
    }
}
