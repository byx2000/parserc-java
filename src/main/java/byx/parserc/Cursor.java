package byx.parserc;

public interface Cursor<E> {
    Cursor<E> next();
    boolean end();
    E current();
}
