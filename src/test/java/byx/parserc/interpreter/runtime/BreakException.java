package byx.parserc.interpreter.runtime;

public class BreakException extends RuntimeException {
    public BreakException() {
        super(null, null, false, false);
    }
}
