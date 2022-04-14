package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Environment;

public class GreaterThan extends BinaryCompareOp {
    public GreaterThan(ArithmeticExpr lhs, ArithmeticExpr rhs) {
        super(lhs, rhs);
    }

    @Override
    public boolean eval(Environment env) {
        return lhs.eval(env) > rhs.eval(env);
    }
}
