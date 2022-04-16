package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Scope;
import byx.parserc.interpreter.runtime.Value;

import java.util.List;
import java.util.stream.Collectors;

public class Array implements Expr {
    private final List<Expr> elems;

    public Array(List<Expr> elems) {
        this.elems = elems;
    }

    @Override
    public Value eval(Scope scope) {
        return Value.of(elems.stream().map(e -> e.eval(scope)).collect(Collectors.toList()));
    }
}
