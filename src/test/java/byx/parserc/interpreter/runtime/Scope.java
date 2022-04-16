package byx.parserc.interpreter.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Scope {
    private final Map<String, Value> vars = new HashMap<>();
    private final Scope next;

    public Scope() {
        this(null);
    }

    public Scope(Scope next) {
        this.next = next;
    }

    public void declareVar(String varName, Value value) {
        if (vars.containsKey(varName)) {
            throw new InterpretException("变量重复定义：" + varName);
        }
        vars.put(varName, value);
    }

    public void setVar(String varName, Value value) {
        if (vars.containsKey(varName)) {
            vars.put(varName, value);
            return;
        }
        if (next == null) {
            throw new InterpretException("变量未定义：" + varName);
        }
        next.setVar(varName, value);
    }

    public Value getVar(String varName) {
        if (vars.containsKey(varName)) {
            return vars.get(varName);
        }
        if (next == null) {
            throw new InterpretException("变量未定义：" + varName);
        }
        return next.getVar(varName);
    }

    public Map<String, Value> getVars() {
        return vars.entrySet().stream()
                .filter(e -> !(e.getValue() instanceof CallableValue))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> processValue(e.getValue())));
    }

    private Value processValue(Value v) {
        if (v instanceof ObjectValue) {
            return new ObjectValue(((ObjectValue) v).getProps().entrySet().stream()
                    .filter(e -> !(e.getValue() instanceof CallableValue))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }
        return v;
    }
}
