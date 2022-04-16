package byx.parserc.interpreter.runtime;

import java.util.List;
import java.util.Map;

public interface Value {
    Value UNDEFINED = new UndefinedValue();

    static Value of(int val) {
        return new IntegerValue(val);
    }

    static Value of (double val) {
        return new DoubleValue(val);
    }

    static Value of(boolean val) {
        return BoolValue.of(val);
    }

    static Value of (String val) {
        return new StringValue(val);
    }

    static Value of(Callable val) {
        return new CallableValue(val);
    }

    static Value of(Map<String, Value> val) {
        return new ObjectValue(val);
    }

    static Value of(List<Value> val) {
        return new ListValue(val);
    }

    default Value add(Value rhs) {
        throw new InterpretException(String.format("unsupported operator + between %s and %s", this, rhs));
    }

    default Value sub(Value rhs) {
        throw new InterpretException(String.format("unsupported operator - between %s and %s", this, rhs));
    }

    default Value mul(Value rhs) {
        throw new InterpretException(String.format("unsupported operator * between %s and %s", this, rhs));
    }

    default Value div(Value rhs) {
        throw new InterpretException(String.format("unsupported operator / between %s and %s", this, rhs));
    }

    default Value rem(Value rhs) {
        throw new InterpretException(String.format("unsupported operator %% between %s and %s", this, rhs));
    }

    default Value lessThan(Value rhs) {
        throw new InterpretException(String.format("unsupported operator < between %s and %s", this, rhs));
    }

    default Value lessEqualThan(Value rhs) {
        throw new InterpretException(String.format("unsupported operator <= between %s and %s", this, rhs));
    }

    default Value greaterThan(Value rhs) {
        throw new InterpretException(String.format("unsupported operator > between %s and %s", this, rhs));
    }

    default Value greaterEqualThan(Value rhs) {
        throw new InterpretException(String.format("unsupported operator >= between %s and %s", this, rhs));
    }

    default Value equal(Value rhs) {
        throw new InterpretException(String.format("unsupported operator == between %s and %s", this, rhs));
    }

    default Value notEqual(Value rhs) {
        throw new InterpretException(String.format("unsupported operator != between %s and %s", this, rhs));
    }

    default Value and(Value rhs) {
        throw new InterpretException(String.format("unsupported operator && between %s and %s", this, rhs));
    }

    default Value or(Value rhs) {
        throw new InterpretException(String.format("unsupported operator || between %s and %s", this, rhs));
    }

    default boolean toCondition() {
        throw new InterpretException(String.format("%s is not condition", this));
    }

    default Value not() {
        throw new InterpretException(String.format("unsupported operator ! on %s", this));
    }

    default Value call(List<Value> args) {
        throw new InterpretException(String.format("%s is not callable", this));
    }

    default Value getProp(String propName) {
        return UNDEFINED;
    }

    default Value subscript(Value sub) {
        return UNDEFINED;
    }
}
