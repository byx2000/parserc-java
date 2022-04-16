package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Scope;

public class EmptyStatement implements Statement {
    @Override
    public void execute(Scope scope) {

    }
}
