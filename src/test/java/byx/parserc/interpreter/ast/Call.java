package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.InterpretException;
import byx.parserc.interpreter.runtime.ReturnException;
import byx.parserc.interpreter.runtime.Scope;
import byx.parserc.interpreter.runtime.Value;

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
        Value v = func.eval(scope);
        if (!v.isFunction()) {
            throw new InterpretException(v.getValue() + "不是函数");
        }
        FunctionValue func = v.getFunction();
        if (func.getParams().size() != params.size()) {
            throw new InterpretException("函数参数个数不匹配：" + v.getValue());
        }

        Scope newScope = new Scope(func.getClosure());
        for (int i = 0; i < params.size(); ++i) {
            newScope.declareVar(func.getParams().get(i), params.get(i).eval(scope));
        }

        try {
            func.getBody().execute(newScope);
        } catch (ReturnException e) {
            return e.getRetVal();
        }

        return Value.undefined();
    }
}
