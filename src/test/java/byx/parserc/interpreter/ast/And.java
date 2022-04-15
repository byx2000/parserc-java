package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.InterpretException;
import byx.parserc.interpreter.runtime.Value;

public class And extends BinaryOp {
    public And(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    @Override
    protected Value doEval(Value v1, Value v2) {
        if (v1.isBool() && v2.isBool()) {
            return Value.of(v1.getBool() && v2.getBool());
        }
        throw new InterpretException(String.format("Unsupported operator && between %s and %s", v1.getValue(), v2.getValue()));
    }
}
