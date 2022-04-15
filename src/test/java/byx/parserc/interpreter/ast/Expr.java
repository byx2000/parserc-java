package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Environment;
import byx.parserc.interpreter.runtime.Value;

public interface Expr {
    Value eval(Environment env);
}
