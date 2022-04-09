package byx.parserc;

/**
 * 解析异常
 */
public class ParseException extends RuntimeException {
    public ParseException(Input pos, String msg) {
        super(msg + "\nat index: " + pos.index());
    }
}
