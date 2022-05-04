package byx.parserc.exception;

import byx.parserc.Input;

/**
 * 解析异常
 */
public class ParseException extends FastException {
    private final Input input;
    private final String msg;

    public ParseException(Input input, String msg) {
        this.input = input;
        this.msg = msg;
    }

    public Input getInput() {
        return input;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String getMessage() {
        return String.format("at row %d, col %d: %s", input.row(), input.col(), msg);
    }
}
