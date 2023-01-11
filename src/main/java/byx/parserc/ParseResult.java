package byx.parserc;

/**
 * 封装解析结果
 * @param <R> 解析结果类型
 */
public class ParseResult<R> {
    private final R result;
    private final Cursor before;
    private final Cursor remain;

    public ParseResult(R result, Cursor before, Cursor remain) {
        this.result = result;
        this.before = before;
        this.remain = remain;
    }

    /**
     * 获取解析结果
     */
    public R getResult() {
        return result;
    }

    /**
     * 获取解析前输入
     */
    public Cursor getBefore() {
        return before;
    }

    /**
     * 获取解析后剩余输入
     */
    public Cursor getRemain() {
        return remain;
    }
}
