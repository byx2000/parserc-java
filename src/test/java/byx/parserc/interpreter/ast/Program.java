package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Environment;
import byx.parserc.interpreter.runtime.Value;

import java.util.List;
import java.util.Map;

public class Program {
    private final List<Statement> stmts;

    public Program(List<Statement> stmts) {
        this.stmts = stmts;
    }

    public Map<String, Value> run() {
        Environment env = new Environment();
        for (Statement s : stmts) {
            s.execute(env);
        }
        return env.getVars();
    }
}
