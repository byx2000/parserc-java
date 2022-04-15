package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.BreakException;
import byx.parserc.interpreter.runtime.ContinueException;
import byx.parserc.interpreter.runtime.Scope;

public class ForLoop implements Statement {
    private final Statement init;
    private final Expr cond;
    private final Statement update;
    private final Statement body;

    public ForLoop(Statement init, Expr cond, Statement update, Statement body) {
        this.init = init;
        this.cond = cond;
        this.update = update;
        this.body = body;
    }

    @Override
    public void execute(Scope scope) {
        scope = new Scope(scope);
        for (init.execute(scope); cond.eval(scope).getBool(); update.execute(scope)) {
            try {
                body.execute(scope);
            } catch (BreakException e) {
                break;
            } catch (ContinueException e) {}
        }
    }
}
