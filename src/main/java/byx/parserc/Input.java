package byx.parserc;

/**
 * 封装解析器输入
 */
public class Input {
    private final String input;
    private final int index;

    public Input(String s, int index) {
        this.input = s;
        this.index = index;
    }

    public Input next() {
        return new Input(input, index + 1);
    }

    public boolean end() {
        return index == input.length();
    }

    public char current() {
        return input.charAt(index);
    }

    public int index() {
        return index;
    }
}
