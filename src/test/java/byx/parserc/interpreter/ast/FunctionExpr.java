package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.*;

import java.util.List;

public class FunctionExpr implements Expr {
    private final List<String> params;
    private final Statement body;

    public FunctionExpr(List<String> params, Statement body) {
        this.params = params;
        this.body = body;
    }

    @Override
    public Value eval(Scope scope) {
        return Value.of(args -> {
            // 传递实参
            Scope newScope = new Scope(scope);
            for (int i = 0; i < params.size(); ++i) {
                if (i < args.size()) {
                    newScope.declareVar(params.get(i), args.get(i));
                } else {
                    newScope.declareVar(params.get(i), Value.UNDEFINED);
                }
            }

            // 执行函数体
            try {
                body.execute(newScope);
            } catch (ReturnException e) {
                return e.getRetVal();
            }
            return Value.UNDEFINED;
        });
    }
}
