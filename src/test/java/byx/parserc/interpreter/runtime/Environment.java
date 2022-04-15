package byx.parserc.interpreter.runtime;

import java.util.*;

public class Environment {
    private final LinkedList<Map<String, Value>> scopes = new LinkedList<>(List.of(new HashMap<>()));

    public void declareVar(String varName, Value value) {
        Map<String, Value> scope = scopes.get(scopes.size() - 1);
        if (scope.containsKey(varName)) {
            throw new InterpretException("变量重复定义：" + varName);
        }
        scope.put(varName, value);
    }

    public void setVar(String varName, Value value) {
        for (int i = scopes.size() - 1; i >= 0; --i) {
            Map<String, Value> scope = scopes.get(i);
            if (scope.containsKey(varName)) {
                scope.put(varName, value);
                return;
            }
        }
        throw new InterpretException("变量未定义：" + varName);
    }

    public Value getVar(String varName) {
        for (int i = scopes.size() - 1; i >= 0; --i) {
            Map<String, Value> scope = scopes.get(i);
            if (scope.containsKey(varName)) {
                return scope.get(varName);
            }
        }
        throw new InterpretException("变量未定义：" + varName);
    }

    public Map<String, Value> getVars() {
        return Collections.unmodifiableMap(scopes.get(scopes.size() - 1));
    }

    public void pushScope() {
        scopes.addLast(new HashMap<>());
    }

    public void popScope() {
        scopes.removeLast();
    }
}
