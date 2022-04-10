package byx.parserc;

/**
 * 解析异常
 */
public class ParseException extends RuntimeException {
    public ParseException(Input input) {
        super(String.format("error at index: %s\nremain input: \n%s", input.index(), input), null, false, false);
    }
}
