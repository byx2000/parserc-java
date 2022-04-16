package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Value;

public class Not extends UnaryOp {
    public Not(Expr expr) {
        super(expr);
    }

    @Override
    protected Value doEval(Value v) {
        return v.not();
    }
}
