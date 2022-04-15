package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Scope;
import byx.parserc.interpreter.runtime.Value;

public class Var implements Expr {
    private final String varName;

    public Var(String varName) {
        this.varName = varName;
    }

    @Override
    public Value eval(Scope scope) {
        return scope.getVar(varName);
    }
}
