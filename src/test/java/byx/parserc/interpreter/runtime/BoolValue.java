package byx.parserc.interpreter.runtime;

import java.util.Objects;

public class BoolValue implements Value {
    private static final BoolValue TRUE = new BoolValue(true);
    private static final BoolValue FALSE = new BoolValue(false);

    private final boolean value;

    private BoolValue(boolean value) {
        this.value = value;
    }

    public static BoolValue of(boolean value) {
        return value ? TRUE : FALSE;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoolValue boolValue = (BoolValue) o;
        return value == boolValue.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.format("Bool{%s}", value);
    }

    @Override
    public Value add(Value rhs) {
        if (rhs instanceof StringValue) {
            return new StringValue(value + ((StringValue) rhs).getValue());
        }
        return Value.super.add(rhs);
    }

    @Override
    public Value and(Value rhs) {
        if (rhs instanceof BoolValue) {
            return new BoolValue(value && ((BoolValue) rhs).getValue());
        }
        return Value.super.and(rhs);
    }

    @Override
    public Value or(Value rhs) {
        if (rhs instanceof BoolValue) {
            return new BoolValue(value || ((BoolValue) rhs).getValue());
        }
        return Value.super.or(rhs);
    }

    @Override
    public Value not() {
        return new BoolValue(!value);
    }

    @Override
    public Value equal(Value rhs) {
        if (rhs instanceof BoolValue) {
            return new BoolValue(value == ((BoolValue) rhs).getValue());
        }
        return new BoolValue(false);
    }

    @Override
    public Value notEqual(Value rhs) {
        if (rhs instanceof BoolValue) {
            return new BoolValue(value != ((BoolValue) rhs).getValue());
        }
        return new BoolValue(true);
    }

    @Override
    public boolean toCondition() {
        return value;
    }
}
