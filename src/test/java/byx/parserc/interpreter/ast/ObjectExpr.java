package byx.parserc.interpreter.ast;

import byx.parserc.interpreter.runtime.Scope;
import byx.parserc.interpreter.runtime.Value;

import java.util.Map;
import java.util.stream.Collectors;

public class ObjectExpr implements Expr {
    private final Map<String, Expr> props;

    public ObjectExpr(Map<String, Expr> props) {
        this.props = props;
    }

    @Override
    public Value eval(Scope scope) {
        return Value.of(props.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().eval(scope))));
    }
}
