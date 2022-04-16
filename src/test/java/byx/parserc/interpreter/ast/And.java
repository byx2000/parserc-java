package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Value;

public class And extends BinaryOp {
    public And(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    @Override
    protected Value doEval(Value v1, Value v2) {
        return v1.and(v2);
    }
}
