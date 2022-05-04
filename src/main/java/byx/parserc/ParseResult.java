package byx.parserc;

/**
 * 封装解析结果
 * @param <R> 解析结果类型
 */
public class ParseResult<R> {
    /**
     * 解析结果
     */
    private final R result;
    /**
     * 剩余输入
     */
    private final Input remain;

    public ParseResult(R result, Input remain) {
        this.result = result;
        this.remain = remain;
    }

    public R getResult() {
        return result;
    }

    public Input getRemain() {
        return remain;
    }

    @Override
    public String toString() {
        return String.format("ParseResult{result=%s, remain='%s'}", result, remain);
    }
}
