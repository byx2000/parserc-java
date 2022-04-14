package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Environment;

public class Equal extends BinaryCompareOp {
    public Equal(ArithmeticExpr lhs, ArithmeticExpr rhs) {
        super(lhs, rhs);
    }

    @Override
    public boolean eval(Environment env) {
        return lhs.eval(env) == rhs.eval(env);
    }
}
