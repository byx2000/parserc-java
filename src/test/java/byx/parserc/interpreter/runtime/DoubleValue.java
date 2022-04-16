package byx.parserc.interpreter.runtime;

import java.util.Objects;

public class DoubleValue implements Value {
    private final double value;

    public DoubleValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoubleValue that = (DoubleValue) o;
        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.format("Double{%s}", value);
    }

    @Override
    public Value add(Value rhs) {
        if (rhs instanceof IntegerValue) {
            return new DoubleValue(value + ((IntegerValue) rhs).getValue());
        } else if (rhs instanceof DoubleValue) {
            return new DoubleValue(value + ((DoubleValue) rhs).getValue());
        } else if (rhs instanceof StringValue) {
            return new StringValue(value + ((StringValue) rhs).getValue());
        }
        return Value.super.add(rhs);
    }

    @Override
    public Value sub(Value rhs) {
        if (rhs instanceof IntegerValue) {
            return new DoubleValue(value - ((IntegerValue) rhs).getValue());
        } else if (rhs instanceof DoubleValue) {
            return new DoubleValue(value - ((DoubleValue) rhs).getValue());
        }
        return Value.super.sub(rhs);
    }

    @Override
    public Value mul(Value rhs) {
        if (rhs instanceof IntegerValue) {
            return new DoubleValue(value * ((IntegerValue) rhs).getValue());
        } else if (rhs instanceof DoubleValue) {
            return new DoubleValue(value * ((DoubleValue) rhs).getValue());
        }
        return Value.super.mul(rhs);
    }

    @Override
    public Value div(Value rhs) {
        if (rhs instanceof IntegerValue) {
            return new DoubleValue(value / ((IntegerValue) rhs).getValue());
        } else if (rhs instanceof DoubleValue) {
            return new DoubleValue(value / ((DoubleValue) rhs).getValue());
        }
        return Value.super.div(rhs);
    }

    @Override
    public Value lessThan(Value rhs) {
        if (rhs instanceof IntegerValue) {
            return BoolValue.of(value < ((IntegerValue) rhs).getValue());
        } else if (rhs instanceof DoubleValue) {
            return BoolValue.of(value < ((DoubleValue) rhs).getValue());
        }
        return Value.super.lessThan(rhs);
    }

    @Override
    public Value lessEqualThan(Value rhs) {
        if (rhs instanceof IntegerValue) {
            return BoolValue.of(value <= ((IntegerValue) rhs).getValue());
        } else if (rhs instanceof DoubleValue) {
            return BoolValue.of(value <= ((DoubleValue) rhs).getValue());
        }
        return Value.super.lessEqualThan(rhs);
    }

    @Override
    public Value greaterThan(Value rhs) {
        if (rhs instanceof IntegerValue) {
            return BoolValue.of(value > ((IntegerValue) rhs).getValue());
        } else if (rhs instanceof DoubleValue) {
            return BoolValue.of(value > ((DoubleValue) rhs).getValue());
        }
        return Value.super.greaterThan(rhs);
    }

    @Override
    public Value greaterEqualThan(Value rhs) {
        if (rhs instanceof IntegerValue) {
            return BoolValue.of(value >= ((IntegerValue) rhs).getValue());
        } else if (rhs instanceof DoubleValue) {
            return BoolValue.of(value >= ((DoubleValue) rhs).getValue());
        }
        return Value.super.greaterEqualThan(rhs);
    }

    @Override
    public Value equal(Value rhs) {
        if (rhs instanceof DoubleValue) {
            return BoolValue.of(value == ((DoubleValue) rhs).getValue());
        }
        return BoolValue.of(false);
    }

    @Override
    public Value notEqual(Value rhs) {
        if (rhs instanceof DoubleValue) {
            return BoolValue.of(value != ((DoubleValue) rhs).getValue());
        }
        return BoolValue.of(true);
    }
}
