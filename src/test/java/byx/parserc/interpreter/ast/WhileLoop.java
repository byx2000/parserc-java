package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.BreakException;
import byx.parserc.interpreter.runtime.ContinueException;
import byx.parserc.interpreter.runtime.Environment;

public class WhileLoop implements Statement {
    private final Expr cond;
    private final Statement body;

    public WhileLoop(Expr cond, Statement body) {
        this.cond = cond;
        this.body = body;
    }

    @Override
    public void execute(Environment env) {
        while (cond.eval(env).getBool()) {
            try {
                body.execute(env);
            } catch (BreakException e) {
                break;
            } catch (ContinueException e) {}
        }
    }
}
