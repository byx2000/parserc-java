package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Environment;

public class Var implements ArithmeticExpr {
    private final String varName;

    public Var(String varName) {
        this.varName = varName;
    }

    @Override
    public int eval(Environment env) {
        return env.getVar(varName);
    }
}
