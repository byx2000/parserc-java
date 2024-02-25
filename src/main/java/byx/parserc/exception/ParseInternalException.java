package byx.parserc.exception;

/**
 * 内部解析异常
 */
public class ParseInternalException extends RuntimeException {
    public static final ParseInternalException INSTANCE = new ParseInternalException();

    private ParseInternalException() {
        // 关闭异常调用栈记录，提高异常创建的速度
        super(null, null, false, false);
    }
}
