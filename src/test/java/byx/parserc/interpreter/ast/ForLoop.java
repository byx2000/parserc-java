package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.BreakException;
import byx.parserc.interpreter.runtime.ContinueException;
import byx.parserc.interpreter.runtime.Environment;

public class ForLoop implements Statement {
    private final Statement init;
    private final ConditionExpr cond;
    private final Statement update;
    private final Statement body;

    public ForLoop(Statement init, ConditionExpr cond, Statement update, Statement body) {
        this.init = init;
        this.cond = cond;
        this.update = update;
        this.body = body;
    }

    @Override
    public void execute(Environment env) {
        env.pushScope();
        for (init.execute(env); cond.eval(env); update.execute(env)) {
            try {
                body.execute(env);
            } catch (BreakException e) {
                break;
            } catch (ContinueException e) {}
        }
        env.popScope();
    }
}
