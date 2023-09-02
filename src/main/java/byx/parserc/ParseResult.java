package byx.parserc;

/**
 * 封装解析结果
 * @param <R> 解析结果类型
 */
public class ParseResult<R> {
    private final R result;
    private final Cursor remain;

    public ParseResult(R result, Cursor remain) {
        this.result = result;
        this.remain = remain;
    }

    /**
     * 获取解析结果
     */
    public R getResult() {
        return result;
    }

    /**
     * 获取解析后剩余输入
     */
    public Cursor getRemain() {
        return remain;
    }
}
