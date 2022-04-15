package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Scope;

public interface Statement {
    void execute(Scope scope);
}
