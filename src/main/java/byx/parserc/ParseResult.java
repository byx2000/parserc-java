package byx.parserc;

public class ParseResult<T, E> {
    private final Cursor<E> remain;
    private final T result;

    private ParseResult(Cursor<E> remain, T result) {
        this.remain = remain;
        this.result = result;
    }

    public static <T, E> ParseResult<T, E> of(Cursor<E> remain, T result) {
        return new ParseResult<>(remain, result);
    }

    public Cursor<E> getRemain() {
        return remain;
    }

    public T getResult() {
        return result;
    }

    @Override
    public String toString() {
        return String.format("ParseResult{result=%s, remain=%s}",
                result.toString(), remain.toString());
    }
}
