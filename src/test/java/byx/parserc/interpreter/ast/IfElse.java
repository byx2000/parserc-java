package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Scope;

public class IfElse implements Statement {
    private final Expr cond;
    private final Statement trueBranch;
    private final Statement falseBranch;

    public IfElse(Expr cond, Statement trueBranch, Statement falseBranch) {
        this.cond = cond;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    @Override
    public void execute(Scope scope) {
        if (cond.eval(scope).getBool()) {
            trueBranch.execute(scope);
        } else {
            falseBranch.execute(scope);
        }
    }
}
