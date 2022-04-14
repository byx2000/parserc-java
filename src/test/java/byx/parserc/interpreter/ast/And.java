package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Environment;

public class And extends BinaryLoginOp {
    public And(ConditionExpr lhs, ConditionExpr rhs) {
        super(lhs, rhs);
    }

    @Override
    public boolean eval(Environment env) {
        return lhs.eval(env) && rhs.eval(env);
    }
}
