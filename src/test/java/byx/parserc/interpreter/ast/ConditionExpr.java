package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Environment;

public interface ConditionExpr {
    boolean eval(Environment env);
}
