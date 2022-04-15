package byx.parserc.interpreter.runtime;

import java.util.Objects;

public class Value {
    private final ValueType type;
    private final Object val;

    private Value(ValueType type, Object val) {
        this.type = type;
        this.val = val;
    }

    public static Value of(int intVal) {
        return new Value(ValueType.Integer, intVal);
    }

    public static Value of(double doubleVal) {
        return new Value(ValueType.Double, doubleVal);
    }

    public static Value of(boolean boolVal) {
        return new Value(ValueType.Bool, boolVal);
    }

    public static Value of(String stringVal) {
        return new Value(ValueType.String, stringVal);
    }

    public ValueType getType() {
        return type;
    }

    public Object getValue() {
        return val;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Value value = (Value) o;
        return type == value.type && Objects.equals(val, value.val);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, val);
    }

    @Override
    public String toString() {
        return "Value{" +
                "type=" + type +
                ", val=" + val +
                '}';
    }

    public boolean isInteger() {
        return type == ValueType.Integer;
    }

    public boolean isDouble() {
        return type == ValueType.Double;
    }

    public boolean isBool() {
        return type == ValueType.Bool;
    }

    public boolean isString() {
        return type == ValueType.String;
    }

    public int getInteger() {
        return (int) val;
    }

    public double getDouble() {
        return (double) val;
    }

    public boolean getBool() {
        return (boolean) val;
    }

    public String getString() {
        return (String) val;
    }
}
