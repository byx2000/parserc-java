package byx.parserc.exception;

/**
 * 关闭异常调用栈记录，提高异常创建的速度
 */
public class FastException extends RuntimeException {
    public FastException() {
        super(null, null, false, false);
    }
}
