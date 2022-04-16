package byx.parserc.interpreter.runtime;

import java.util.List;

public interface Callable {
    Value call(List<Value> args);
}
