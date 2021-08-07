package byx.parserc;

import java.util.List;
import java.util.function.Function;

public interface Parser<T, E> {
    ParseResult<T, E> parse(Cursor<E> cursor) throws ParseException;

    default <U> Parser<Pair<T, U>, E> concat(Parser<U, E> rhs) {
        return Parsers.concat(this, rhs);
    }

    default Parser<T, E> or(Parser<T, E> rhs) {
        return Parsers.or(this, rhs);
    }

    default Parser<T, E> end() {
        return Parsers.skipSecond(this, Parsers.end());
    }

    default Parser<List<T>, E> zeroOrMore() {
        return Parsers.zeroOrMore(this);
    }

    default Parser<List<T>, E> oneOrMore() {
        return Parsers.oneOrMore(this);
    }

    default <U> Parser<U, E> map(Function<T, U> mapper) {
        return Parsers.map(this, mapper);
    }

    default <U> Parser<T, E> skip(Parser<U, E> rhs) {
        return Parsers.skipSecond(this, rhs);
    }

    default <U> Parser<U, E> ignore(U value) {
        return Parsers.ignore(this, value);
    }

    default <U> Parser<U, E> ignore() {
        return Parsers.ignore(this);
    }
}
