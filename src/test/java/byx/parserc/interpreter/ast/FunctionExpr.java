package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.FunctionValue;
import byx.parserc.interpreter.runtime.Scope;
import byx.parserc.interpreter.runtime.Value;

import java.util.List;

public class FunctionExpr implements Expr {
    private final List<String> params;
    private final Statement body;

    public FunctionExpr(List<String> params, Statement body) {
        this.params = params;
        this.body = body;
    }

    @Override
    public Value eval(Scope scope) {
        return Value.of(new FunctionValue(params, body, scope));
    }
}
