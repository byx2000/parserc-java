package byx.parserc.exception;

import byx.parserc.Cursor;

/**
 * 严重的解析错误，不会被or和oneOf等组合子捕获
 */
public class FatalParseException extends FastException {
    private final Cursor cursor;
    private final String msg;

    public FatalParseException(Cursor cursor, String msg) {
        this.cursor = cursor;
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return String.format("at row %d, col %d: %s", cursor.row(), cursor.col(), msg);
    }
}
