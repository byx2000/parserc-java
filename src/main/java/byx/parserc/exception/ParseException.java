package byx.parserc.exception;

import byx.parserc.Cursor;

/**
 * 解析异常
 */
public class ParseException extends FastException {
    private final Cursor cursor;
    private final String msg;

    public ParseException(Cursor cursor) {
        this(cursor, "");
    }

    public ParseException(Cursor cursor, String msg) {
        this.cursor = cursor;
        this.msg = msg;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String getMessage() {
        return msg != null && !msg.isBlank()
                ? String.format("parse error at row %d, col %d: %s", cursor.row(), cursor.col(), msg)
                : String.format("parse error at row %d, col %d", cursor.row(), cursor.col());

    }
}
