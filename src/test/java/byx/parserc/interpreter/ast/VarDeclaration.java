package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Environment;

public class VarDeclaration implements Statement {
    private final String varName;
    private final ArithmeticExpr expr;

    public VarDeclaration(String varName, ArithmeticExpr expr) {
        this.varName = varName;
        this.expr = expr;
    }

    @Override
    public void execute(Environment env) {
        env.declareVar(varName, expr.eval(env));
    }
}
