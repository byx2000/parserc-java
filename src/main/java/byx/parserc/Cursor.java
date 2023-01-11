package byx.parserc;

import byx.parserc.exception.EndOfInputException;

/**
 * 封装当前解析位置
 */
public class Cursor {
    private final String s;
    private final int index;
    private final int row, col;

    public Cursor(String s) {
        this(s, 0);
    }

    public Cursor(String s, int index) {
        this(s, index, 1, 1);
    }

    private Cursor(String s, int index, int row, int col) {
        this.s = s;
        this.index = index;
        this.row = row;
        this.col = col;
    }

    public Cursor next() throws EndOfInputException {
        if (end()) {
            throw new EndOfInputException(this);
        }

        int row = this.row;
        int col = this.col + 1;
        if (s.charAt(index) == '\n') {
            row++;
            col = 1;
        }
        return new Cursor(s, index + 1, row, col);
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
    public char current() throws EndOfInputException {
        if (end()) {
            throw new EndOfInputException(this);
        }
        return s.charAt(index);
    }

    /**
     * 获取当前索引
     * @return 当前索引
     */
    public int getIndex() {
        return index;
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
