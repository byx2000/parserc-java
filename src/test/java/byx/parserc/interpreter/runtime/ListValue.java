package byx.parserc.interpreter.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ListValue implements Value {
    private final List<Value> value;
    private final Map<String, Value> PROPS;

    public ListValue(List<Value> value) {
        this.value = new ArrayList<>(value);
        PROPS = Map.of(
                "add", Value.of(args -> {
                    if (args.size() != 1) {
                        throw new InterpretException("add method require 1 argument");
                    }
                    this.value.add(args.get(0));
                    return Value.UNDEFINED;
                }),
                "remove", Value.of(args -> {
                    if (args.size() != 1 || !(args.get(0) instanceof IntegerValue)) {
                        throw new InterpretException("remove method require 1 integer argument");
                    }
                    int index = ((IntegerValue) args.get(0)).getValue();
                    return this.value.remove(index);
                }),
                "length", Value.of(args -> Value.of(this.value.size()))
        );
    }

    public List<Value> getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListValue listValue = (ListValue) o;
        return Objects.equals(value, listValue.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.format("List{%s}", value);
    }

    @Override
    public Value getProp(String propName) {
        if (PROPS.containsKey(propName)) {
            return PROPS.get(propName);
        }
        return Value.super.getProp(propName);
    }

    @Override
    public Value subscript(Value sub) {
        if (sub instanceof IntegerValue) {
            int index = ((IntegerValue) sub).getValue();
            return value.get(index);
        }
        return Value.super.subscript(sub);
    }
}
