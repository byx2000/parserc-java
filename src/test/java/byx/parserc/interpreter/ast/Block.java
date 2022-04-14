package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Environment;

import java.util.List;

public class Block implements Statement {
    private final List<Statement> stmts;

    public Block(List<Statement> stmts) {
        this.stmts = stmts;
    }

    @Override
    public void execute(Environment env) {
        env.pushScope();
        try {
            stmts.forEach(s -> s.execute(env));
        } finally {
            env.popScope();
        }
    }
}
