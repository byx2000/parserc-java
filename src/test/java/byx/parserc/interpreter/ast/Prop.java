package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Scope;
import byx.parserc.interpreter.runtime.Value;

public class Prop implements Expr {
    private final Expr obj;
    private final String propName;

    public Prop(Expr obj, String propName) {
        this.obj = obj;
        this.propName = propName;
    }

    @Override
    public Value eval(Scope scope) {
        return obj.eval(scope).getProp(propName);
    }
}
