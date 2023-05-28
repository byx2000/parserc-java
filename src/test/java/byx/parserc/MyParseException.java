package byx.parserc;

public class MyParseException extends RuntimeException {
    private final Cursor cursor;

    public MyParseException(Cursor cursor, String msg) {
        super(msg);
        this.cursor = cursor;
    }

    public MyParseException(String msg) {
        this(null, msg);
    }

    public Cursor getCursor() {
        return cursor;
    }
}
