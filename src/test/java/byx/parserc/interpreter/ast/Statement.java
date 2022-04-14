package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Environment;

public interface Statement {
    void execute(Environment env);
}
