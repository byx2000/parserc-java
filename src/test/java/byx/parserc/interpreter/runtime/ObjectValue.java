package byx.parserc.interpreter.runtime;

import java.util.Map;
import java.util.Objects;

public class ObjectValue implements Value {
    private final Map<String, Value> props;

    public ObjectValue(Map<String, Value> props) {
        this.props = props;
    }

    public Map<String, Value> getProps() {
        return props;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectValue that = (ObjectValue) o;
        return Objects.equals(props, that.props);
    }

    @Override
    public int hashCode() {
        return Objects.hash(props);
    }

    @Override
    public String toString() {
        return String.format("Object{%s}", props);
    }

    @Override
    public Value getProp(String propName) {
        if (props.containsKey(propName)) {
            return props.get(propName);
        }
        return Value.super.getProp(propName);
    }
}
