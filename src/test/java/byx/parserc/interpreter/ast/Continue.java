package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.ContinueException;
import byx.parserc.interpreter.runtime.Scope;

public class Continue implements Statement {
    private static final ContinueException CONTINUE_EXCEPTION = new ContinueException();

    @Override
    public void execute(Scope scope) {
        throw CONTINUE_EXCEPTION;
    }
}
