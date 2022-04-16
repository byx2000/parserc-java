package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.*;

import java.util.List;

public class Call implements Expr {
    private final Expr func;
    private final List<Expr> params;

    public Call(Expr func, List<Expr> params) {
        this.func = func;
        this.params = params;
    }

    @Override
    public Value eval(Scope scope) {
        // 获取函数运行时信息
        Value v = func.eval(scope);
        if (!v.isFunction()) {
            throw new InterpretException(v.getValue() + "不是函数");
        }
        FunctionValue func = v.getFunction();

        // 传递实参
        Scope newScope = new Scope(func.getClosure());
        List<String> paramNames = func.getParams();
        for (int i = 0; i < paramNames.size(); ++i) {
            if (i < params.size()) {
                newScope.declareVar(paramNames.get(i), this.params.get(i).eval(scope));
            } else {
                newScope.declareVar(paramNames.get(i), Value.undefined());
            }
        }

        // 执行函数体
        try {
            func.getBody().execute(newScope);
        } catch (ReturnException e) {
            return e.getRetVal();
        }

        return Value.undefined();
    }
}
