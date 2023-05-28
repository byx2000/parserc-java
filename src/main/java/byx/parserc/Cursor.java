package byx.parserc;


import byx.parserc.exception.ParseException;

/**
 * 封装当前解析位置
 */
public final class Cursor {
    private final String s;
    private final int index;
    private final int row, col;

    public Cursor(String s) {
        this(s, 0, 1, 1);
    }

    private Cursor(String s, int index, int row, int col) {
        this.s = s;
        this.index = index;
        this.row = row;
        this.col = col;
    }

    public Cursor next() throws ParseException {
        if (end()) {
            throw new ParseException(this, "unexpected end of input");
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
     */
    public boolean end() {
        return index == s.length();
    }

    /**
     * 获取当前字符
     */
    public char current() throws ParseException {
        if (end()) {
            throw new ParseException(this, "unexpected end of input");
        }
        return s.charAt(index);
    }

    /**
     * 获取当前索引
     */
    public int index() {
        return index;
    }

    /**
     * 获取当前行号
     */
    public int row() {
        return row;
    }

    /**
     * 获取当前列号
     */
    public int col() {
        return col;
    }
}
