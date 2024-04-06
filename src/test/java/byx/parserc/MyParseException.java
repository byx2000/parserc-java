package byx.parserc;

/**
 * 自定义解析异常
 */
public class MyParseException extends RuntimeException {
    private final String input;
    private final Integer index;
    private final String msg;
    private int row;
    private int col;

    public MyParseException(String input, Integer index, String msg) {
        this.input = input;
        this.index = index;
        this.msg = msg;

        if (input != null && index != null) {
            Pair<Integer, Integer> rowAndCol = getRowAndCol();
            this.row = rowAndCol.first();
            this.col = rowAndCol.second();
        }
    }

    public MyParseException(String msg) {
        this(null, null, msg);
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String getMessage() {
        if (input != null && index != null) {
            return String.format("at row %d, col%d: %s", row, col, msg);
        } else {
            return msg;
        }
    }

    private Pair<Integer, Integer> getRowAndCol() {
        int row = 1;
        int col = 0;
        for (int i = 0; i <= index && index < input.length(); i++) {
            if (input.charAt(i) == '\n') {
                row++;
                col = 0;
            }
            col++;
        }
        return new Pair<>(row, col);
    }
}
