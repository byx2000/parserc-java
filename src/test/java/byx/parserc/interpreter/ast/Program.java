package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Scope;
import byx.parserc.interpreter.runtime.Value;

import java.util.List;
import java.util.Map;

public class Program {
    private final List<Statement> stmts;

    public Program(List<Statement> stmts) {
        this.stmts = stmts;
    }

    public Map<String, Value> run() {
        Scope scope = new Scope();
        for (Statement s : stmts) {
            s.execute(scope);
        }
        return scope.getVars();
    }
}
