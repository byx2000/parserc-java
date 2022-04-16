package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.InterpretException;
import byx.parserc.interpreter.runtime.Scope;
import byx.parserc.interpreter.runtime.Value;

import java.util.Map;

public class Prop implements Expr {
    private final Expr obj;
    private final String propName;

    public Prop(Expr obj, String propName) {
        this.obj = obj;
        this.propName = propName;
    }

    @Override
    public Value eval(Scope scope) {
        Value v = obj.eval(scope);
        if (!v.isObject()) {
            throw new InterpretException(v.getValue() + "不是对象");
        }
        Map<String, Value> props = v.getObject();
        if (!props.containsKey(propName)) {
            throw new InterpretException("属性不存在：" + propName);
        }
        return props.get(propName);
    }
}
