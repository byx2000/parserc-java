package byx.parserc.interpreter.runtime;

import java.util.Map;
import java.util.Objects;

public class StringValue implements Value {
    private final String value;
    private final Map<String, Value> PROPS;

    public StringValue(String value) {
        this.value = value;
        PROPS = Map.of(
                "length", Value.of(args -> Value.of(value.length())),
                "substring", Value.of(args -> {
                    if (args.size() != 2 || !(args.get(0) instanceof IntegerValue) || !(args.get(1) instanceof IntegerValue)) {
                        throw new InterpretException("substring method require 2 integer arguments");
                    }
                    int begin = ((IntegerValue) args.get(0)).getValue();
                    int end = ((IntegerValue) args.get(1)).getValue();
                    return Value.of(value.substring(begin, end));
                }),
                "concat", Value.of(args -> {
                    if (args.size() != 1 || !(args.get(0) instanceof StringValue)) {
                        throw new InterpretException("concat method require 1 string arguments");
                    }
                    String s = ((StringValue) args.get(0)).getValue();
                    return Value.of(value.concat(s));
                }),
                "toInt", Value.of(args -> Value.of(Integer.parseInt(value))),
                "toDouble", Value.of(args -> Value.of(Double.parseDouble(value))),
                "toBool", Value.of(args -> Value.of(Boolean.parseBoolean(value)))
        );
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringValue that = (StringValue) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.format("String{%s}", value);
    }

    @Override
    public Value add(Value rhs) {
        if (rhs instanceof StringValue) {
            return new StringValue(value + ((StringValue) rhs).getValue());
        } else if (rhs instanceof IntegerValue) {
            return new StringValue(value + ((IntegerValue) rhs).getValue());
        } else if (rhs instanceof DoubleValue) {
            return new StringValue(value + ((DoubleValue) rhs).getValue());
        } else if (rhs instanceof BoolValue) {
            return new StringValue(value + ((BoolValue) rhs).getValue());
        }
        return Value.super.add(rhs);
    }

    @Override
    public Value lessThan(Value rhs) {
        if (rhs instanceof StringValue) {
            return BoolValue.of(value.compareTo(((StringValue) rhs).getValue()) < 0);
        }
        return Value.super.lessThan(rhs);
    }

    @Override
    public Value lessEqualThan(Value rhs) {
        if (rhs instanceof StringValue) {
            return BoolValue.of(value.compareTo(((StringValue) rhs).getValue()) <= 0);
        }
        return Value.super.lessEqualThan(rhs);
    }

    @Override
    public Value greaterThan(Value rhs) {
        if (rhs instanceof StringValue) {
            return BoolValue.of(value.compareTo(((StringValue) rhs).getValue()) > 0);
        }
        return Value.super.greaterThan(rhs);
    }

    @Override
    public Value greaterEqualThan(Value rhs) {
        if (rhs instanceof StringValue) {
            return BoolValue.of(value.compareTo(((StringValue) rhs).getValue()) >= 0);
        }
        return Value.super.greaterEqualThan(rhs);
    }

    @Override
    public Value equal(Value rhs) {
        if (rhs instanceof StringValue) {
            return BoolValue.of(value.compareTo(((StringValue) rhs).getValue()) == 0);
        }
        return BoolValue.of(false);
    }

    @Override
    public Value notEqual(Value rhs) {
        if (rhs instanceof StringValue) {
            return BoolValue.of(value.compareTo(((StringValue) rhs).getValue()) != 0);
        }
        return BoolValue.of(true);
    }

    @Override
    public Value getProp(String propName) {
        if (PROPS.containsKey(propName)) {
            return PROPS.get(propName);
        }
        return Value.super.getProp(propName);
    }
}
