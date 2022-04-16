package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Value;

public class Add extends BinaryOp {
    public Add(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    @Override
    protected Value doEval(Value v1, Value v2) {
        return v1.add(v2);
    }
}
