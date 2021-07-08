package byx.parserc;

public class StringInputCursor implements Cursor<Character> {
    private final String input;
    private final int index;

    public StringInputCursor(String input) {
        this(input, 0);
    }

    private StringInputCursor(String input, int index) {
        this.input = input;
        this.index = index;
    }

    @Override
    public Cursor<Character> next() {
        return new StringInputCursor(input, index + 1);
    }

    @Override
    public boolean end() {
        return index == input.length();
    }

    @Override
    public Character current() {
        return input.charAt(index);
    }

    @Override
    public String toString() {
        return input.substring(index);
    }
}
