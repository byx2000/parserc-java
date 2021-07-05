package byx.parserc;

public class ParseResult<T> {
    private final Cursor remain;
    private final T result;

    public ParseResult(Cursor remain, T result) {
        this.remain = remain;
        this.result = result;
    }

    public Cursor getRemain() {
        return remain;
    }

    public T getResult() {
        return result;
    }

    @Override
    public String toString() {
        return String.format("ParseResult{result=%s, remain=%s}", result, remain);
    }
}
