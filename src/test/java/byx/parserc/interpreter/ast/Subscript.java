package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Scope;
import byx.parserc.interpreter.runtime.Value;

public class Subscript implements Expr {
    private final Expr arr;
    private final Expr sub;

    public Subscript(Expr arr, Expr sub) {
        this.arr = arr;
        this.sub = sub;
    }

    @Override
    public Value eval(Scope scope) {
        return arr.eval(scope).subscript(sub.eval(scope));
    }
}
