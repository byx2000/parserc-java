package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.ReturnException;
import byx.parserc.interpreter.runtime.Scope;
import byx.parserc.interpreter.runtime.Value;

public class Return implements Statement {
    private final Expr retVal;

    public Return(Expr retVal) {
        this.retVal = retVal;
    }

    @Override
    public void execute(Scope scope) {
        if (retVal != null) {
            throw new ReturnException(retVal.eval(scope));
        }
        throw new ReturnException(Value.UNDEFINED);
    }
}
