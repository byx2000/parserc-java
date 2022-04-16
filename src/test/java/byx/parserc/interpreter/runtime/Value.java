package byx.parserc.interpreter.runtime;

import java.util.Map;
import java.util.Objects;

public class Value {
    private final ValueType type;
    private final Object val;

    private static final Value TRUE = new Value(ValueType.Bool, true);
    private static final Value FALSE = new Value(ValueType.Bool, false);
    private static final Value UNDEFINED = new Value(ValueType.Undefined, null);

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
        return boolVal ? TRUE : FALSE;
    }

    public static Value of(String stringVal) {
        return new Value(ValueType.String, stringVal);
    }

    public static Value of(FunctionValue functionVal) {
        return new Value(ValueType.Function, functionVal);
    }

    public static Value of(Map<String, Value> props) {
        return new Value(ValueType.Object, props);
    }

    public static Value undefined() {
        return UNDEFINED;
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

    public boolean isFunction() {
        return type == ValueType.Function;
    }

    public boolean isObject() {
        return type == ValueType.Object;
    }

    public boolean isUndefined() {
        return type == ValueType.Undefined;
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

    public FunctionValue getFunction() {
        return (FunctionValue) val;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Value> getObject() {
        return (Map<String, Value>) val;
    }
}
