package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Environment;

public class IfElse implements Statement {
    private final ConditionExpr cond;
    private final Statement trueBranch;
    private final Statement falseBranch;

    public IfElse(ConditionExpr cond, Statement trueBranch, Statement falseBranch) {
        this.cond = cond;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    @Override
    public void execute(Environment env) {
        if (cond.eval(env)) {
            trueBranch.execute(env);
        } else if (falseBranch != null) {
            falseBranch.execute(env);
        }
    }
}
