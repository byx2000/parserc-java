package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.InterpretException;
import byx.parserc.interpreter.runtime.Value;

public class Add extends BinaryOp {
    public Add(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    @Override
    protected Value doEval(Value v1, Value v2) {
        if (v1.isString() || v2.isString()) {
            return Value.of(v1.getValue().toString() + v2.getValue().toString());
        }

        if (v1.isBool() || v2.isBool()) {
            throw new InterpretException(String.format("Unsupported operator + between %s and %s", v1.getValue(), v2.getValue()));
        }

        if (v1.isInteger() && v2.isInteger()) {
            return Value.of(v1.getInteger() + v2.getInteger());
        } else if (v1.isInteger() && v2.isDouble()) {
            return Value.of(v1.getInteger() + v2.getDouble());
        } else if (v1.isDouble() && v2.isInteger()) {
            return Value.of(v1.getDouble() + v2.getInteger());
        } else {
            return Value.of(v1.getDouble() + v2.getDouble());
        }
    }
}
