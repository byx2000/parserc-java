package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Scope;
import byx.parserc.interpreter.runtime.Value;

public abstract class UnaryOp implements Expr {
    private final Expr expr;

    protected UnaryOp(Expr expr) {
        this.expr = expr;
    }

    protected abstract Value doEval(Value v);

    @Override
    public Value eval(Scope scope) {
        return doEval(expr.eval(scope));
    }
}
