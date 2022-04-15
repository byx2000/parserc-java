package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Value;

import java.util.Objects;

public class Equal extends BinaryOp {
    public Equal(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    @Override
    protected Value doEval(Value v1, Value v2) {
        return Value.of(Objects.equals(v1.getValue(), v2.getValue()));
    }
}