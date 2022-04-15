package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.BreakException;
import byx.parserc.interpreter.runtime.ContinueException;
import byx.parserc.interpreter.runtime.Scope;

public class WhileLoop implements Statement {
    private final Expr cond;
    private final Statement body;

    public WhileLoop(Expr cond, Statement body) {
        this.cond = cond;
        this.body = body;
    }

    @Override
    public void execute(Scope scope) {
        while (cond.eval(scope).getBool()) {
            try {
                body.execute(scope);
            } catch (BreakException e) {
                break;
            } catch (ContinueException e) {}
        }
    }
}
