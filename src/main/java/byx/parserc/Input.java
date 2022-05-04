package byx.parserc;

/**
 * 封装解析器输入
 */
public class Input {
    private final String s;
    private final int index;
    private final int row, col;

    public Input(String s, int index) {
        this(s, index, 1, 1);
    }

    private Input(String s, int index, int row, int col) {
        this.s = s;
        this.index = index;
        this.row = row;
        this.col = col;
    }

    public Input next() {
        int row = this.row;
        int col = this.col + 1;
        if (s.charAt(index) == '\n') {
            row++;
            col = 1;
        }
        return new Input(s, index + 1, row, col);
    }

    /**
     * 是否到达输入结尾
     * @return 是否到达结尾
     */
    public boolean end() {
        return index == s.length();
    }

    /**
     * 获取当前字符
     * @return 当前字符
     */
    public char current() {
        return s.charAt(index);
    }

    /**
     * 获取当前行号
     * @return 当前行号
     */
    public int row() {
        return row;
    }

    /**
     * 获取当前列号
     * @return 当前列号
     */
    public int col() {
        return col;
    }

    @Override
    public String toString() {
        return s.substring(index);
    }
}
