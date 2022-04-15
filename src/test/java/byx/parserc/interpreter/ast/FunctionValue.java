package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Scope;

import java.util.List;

public class FunctionValue {
    private final List<String> params;
    private final Statement body;
    private final Scope closure;

    public FunctionValue(List<String> params, Statement body, Scope closure) {
        this.params = params;
        this.body = body;
        this.closure = closure;
    }

    public List<String> getParams() {
        return params;
    }

    public Statement getBody() {
        return body;
    }

    public Scope getClosure() {
        return closure;
    }
}
