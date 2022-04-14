package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Environment;

public interface ArithmeticExpr {
    int eval(Environment env);
}
