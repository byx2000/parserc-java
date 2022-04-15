package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Scope;

public class VarAssign implements Statement {
    private final String varName;
    private final Expr expr;

    public VarAssign(String varName, Expr expr) {
        this.varName = varName;
        this.expr = expr;
    }

    @Override
    public void execute(Scope scope) {
        scope.setVar(varName, expr.eval(scope));
    }
}
