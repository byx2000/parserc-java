package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Environment;

public class Mul extends BinaryArithmeticOp {
    public Mul(ArithmeticExpr lhs, ArithmeticExpr rhs) {
        super(lhs, rhs);
    }

    @Override
    public int eval(Environment env) {
        return lhs.eval(env) * rhs.eval(env);
    }
}
