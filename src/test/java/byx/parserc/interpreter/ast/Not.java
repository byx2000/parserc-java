package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.InterpretException;
import byx.parserc.interpreter.runtime.Value;

public class Not extends UnaryOp {
    public Not(Expr expr) {
        super(expr);
    }

    @Override
    protected Value doEval(Value v) {
        if (v.isBool()) {
            return Value.of(!v.getBool());
        }
        throw new InterpretException(String.format("Unsupported operator ! on %s", v.getValue()));
    }
}
