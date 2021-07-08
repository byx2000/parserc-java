package byx.parserc;

public interface Parser<T, E> {
    ParseResult<T, E> parse(Cursor<E> cursor) throws ParseException;
}
