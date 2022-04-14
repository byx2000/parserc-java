package byx.parserc.interpreter.ast;

public abstract class BinaryArithmeticOp implements ArithmeticExpr {
    protected final ArithmeticExpr lhs, rhs;

    protected BinaryArithmeticOp(ArithmeticExpr lhs, ArithmeticExpr rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }
}
