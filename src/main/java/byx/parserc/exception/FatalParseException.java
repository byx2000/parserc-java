package byx.parserc.exception;

import byx.parserc.Input;

/**
 * 严重的解析错误，不会被or和oneOf等组合子捕获
 */
public class FatalParseException extends FastException {
    private final Input input;
    private final String msg;

    public FatalParseException(Input input, String msg) {
        this.input = input;
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return String.format("at row %d, col %d: %s", input.row(), input.col(), msg);
    }
}
