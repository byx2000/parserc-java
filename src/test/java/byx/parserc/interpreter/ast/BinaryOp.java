package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Environment;
import byx.parserc.interpreter.runtime.Value;

public abstract class BinaryOp implements Expr {
    private final Expr lhs, rhs;

    protected BinaryOp(Expr lhs, Expr rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    protected abstract Value doEval(Value v1, Value v2);

    @Override
    public Value eval(Environment env) {
        return doEval(lhs.eval(env), rhs.eval(env));
    }
}
