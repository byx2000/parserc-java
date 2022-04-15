package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.BreakException;
import byx.parserc.interpreter.runtime.Scope;

public class Break implements Statement {
    private static final BreakException BREAK_EXCEPTION = new BreakException();

    @Override
    public void execute(Scope scope) {
        throw BREAK_EXCEPTION;
    }
}
