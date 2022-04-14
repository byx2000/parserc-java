package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Environment;

public class Not implements ConditionExpr {
    private final ConditionExpr e;

    public Not(ConditionExpr e) {
        this.e = e;
    }

    @Override
    public boolean eval(Environment env) {
        return !e.eval(env);
    }
}
