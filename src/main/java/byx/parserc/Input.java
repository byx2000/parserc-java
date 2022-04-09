package byx.parserc;

/**
 * 封装解析器输入
 */
public class Input {
    private final String s;
    private final int index;

    public Input(String s, int index) {
        this.s = s;
        this.index = index;
    }

    public Input next() {
        return new Input(s, index + 1);
    }

    public boolean end() {
        return index == s.length();
    }

    public char current() {
        return s.charAt(index);
    }

    public int index() {
        return index;
    }
}
