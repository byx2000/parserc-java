package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.*;

import java.util.List;
import java.util.stream.Collectors;

public class Call implements Expr {
    private final Expr callable;
    private final List<Expr> args;

    public Call(Expr callable, List<Expr> args) {
        this.callable = callable;
        this.args = args;
    }

    @Override
    public Value eval(Scope scope) {
        return callable.eval(scope).call(args.stream().map(p -> p.eval(scope)).collect(Collectors.toList()));
    }
}
