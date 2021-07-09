package byx.parserc;

public interface Parser<T, E> {
    ParseResult<T, E> parse(Cursor<E> cursor) throws ParseException;

    default <U> Parser<Pair<T, U>, E> concat(Parser<U, E> rhs) {
        return Parsers.concat(this, rhs);
    }

    default Parser<T, E> or(Parser<T, E> rhs) {
        return Parsers.or(this, rhs);
    }
}
