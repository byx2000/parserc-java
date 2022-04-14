package byx.parserc.interpreter.ast;

public abstract class BinaryLoginOp implements ConditionExpr {
    protected final ConditionExpr lhs, rhs;

    protected BinaryLoginOp(ConditionExpr lhs, ConditionExpr rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }
}
