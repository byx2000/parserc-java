package byx.parserc.interpreter.ast;

public abstract class BinaryCompareOp implements ConditionExpr {
    protected final ArithmeticExpr lhs, rhs;

    protected BinaryCompareOp(ArithmeticExpr lhs, ArithmeticExpr rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }
}
