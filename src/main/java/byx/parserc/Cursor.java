package byx.parserc;

public interface Cursor<E> {
    Cursor<E> next();
    boolean end();
    E current();

    static Cursor<Character> of(String input) {
        return new StringInputCursor(input);
    }
}
