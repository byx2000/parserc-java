package byx.parserc;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Parser<T> {
    ParseResult<T> parse(Cursor cursor) throws ParseException;

    default ParseResult<T> parse(String input) throws ParseException {
        return parse(new Cursor(input, 0));
    }

    default <U> Parser<Pair<T, U>> concat(Parser<U> rhs) {
        return Parsers.concat(this, rhs);
    }

    default <U> Parser<Pair<T, U>> next(Function<ParseResult<T>, Parser<U>> binder) {
        return Parsers.bind(this, binder);
    }

    default Parser<T> or(Parser<T> rhs) {
        return Parsers.or(this, rhs);
    }

    default Parser<List<T>> oneOrMore() {
        return Parsers.oneOrMore(this);
    }

    default Parser<List<T>> zeroOrMore() {
        return Parsers.zeroOrMore(this);
    }

    default <U> Parser<U> map(Function<T, U> mapper) {
        return Parsers.map(this, mapper);
    }

    default Parser<T> end() {
        return this.skip(Parsers.end());
    }

    default <U> Parser<T> skip(Parser<U> rhs) {
        return Parsers.skipSecond(this, rhs);
    }

    default <U> Parser<U> discard() {
        return Parsers.discard(this);
    }

    default <U> Parser<Pair<T, Pair<String, U>>> until(Parser<U> parser) {
        return this.concat(Parsers.until(parser));
    }

    default <U> Parser<Pair<T, String>> until(Parsers.SkipWrapper<U> skip) {
        return this.concat(Parsers.until(skip));
    }

    default Parser<T> callback(Consumer<ParseResult<T>> onSuccess) {
        return Parsers.callback(this, onSuccess);
    }
}
