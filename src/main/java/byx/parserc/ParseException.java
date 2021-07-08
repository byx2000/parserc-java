package byx.parserc;

public class ParseException extends Exception {
    private final Cursor<?> cursor;
    private final String msg;

    public ParseException(Cursor<?> cursor, String msg) {
        // 取消生成调用栈信息
        super(null, null, false, false);
        this.cursor = cursor;
        this.msg = msg;
    }

    public ParseException(Cursor<?> cursor) {
        this(cursor, "解析错误");
    }

    @Override
    public String getMessage() {
        return msg + "\n剩余输入：" + cursor.toString();
    }
}
