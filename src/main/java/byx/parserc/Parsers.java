package byx.parserc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Parsers {
    public static Parser<Object> empty() {
        return cursor -> new ParseResult<>(cursor, null);
    }

    public static Parser<Character> ch(char c) {
        return satisfy(cc -> cc == c);
    }

    public static Parser<Character> range(char c1, char c2) {
        return satisfy(c -> (c - c1) * (c - c2) <= 0);
    }

    public static Parser<Character> any() {
        return satisfy(c -> true);
    }

    public static Parser<Character> not(char ch) {
        return satisfy(c -> c != ch);
    }

    public static Parser<Character> satisfy(Predicate<Character> predicate) {
        return cursor -> {
            if (cursor.end()) {
                throw new ParseException(cursor);
            }
            if (!predicate.test(cursor.current())) {
                throw new ParseException(cursor);
            }
            return new ParseResult<>(cursor.next(), cursor.current());
        };
    }

    public static Parser<String> literal(String prefix) {
        return literal(prefix, true);
    }

    public static Parser<String> literal(String prefix, boolean caseSensitive) {
        return cursor -> {
            for (int i = 0; i < prefix.length(); ++i) {
                if (cursor.end()) {
                    throw new ParseException(cursor);
                }
                char c1 = prefix.charAt(i), c2 = cursor.current();
                if (caseSensitive) {
                    if (c1 != c2) {
                        throw new ParseException(cursor);
                    }
                } else {
                    if (Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                        throw new ParseException(cursor);
                    }
                }
                cursor = cursor.next();
            }
            return new ParseResult<>(cursor, prefix);
        };
    }

    public static <T> Parser<T> end() {
        return cursor -> {
            if (!cursor.end()) {
                throw new ParseException(cursor);
            }
            return new ParseResult<>(cursor, null);
        };
    }

    public static <T> Parser<Pair<String, T>> until(Parser<T> parser) {
        return cursor -> {
            StringBuilder sb = new StringBuilder();
            while (true) {
                try {
                    ParseResult<T> r = parser.parse(cursor);
                    return new ParseResult<>(r.getRemain(), new Pair<>(sb.toString(), r.getResult()));
                } catch (ParseException e) {
                    if (cursor.end()) {
                        throw new ParseException(cursor);
                    }
                    sb.append(cursor.current());
                    cursor = cursor.next();
                }
            }
        };
    }

    public static class SkipWrapper<T> {
        private final Parser<T> parser;

        public SkipWrapper(Parser<T> parser) {
            this.parser = parser;
        }

        public Parser<T> getParser() {
            return parser;
        }

        public <U> Parser<U> concat(Parser<U> rhs) {
            return Parsers.skipFirst(parser, rhs);
        }
    }

    public static <T> Parser<String> until(SkipWrapper<T> skip) {
        return map(until(skip.getParser()), Pair::getFirst);
    }

    public static <T, U> Parser<Pair<T, U>> concat(Parser<T> lhs, Parser<U> rhs) {
        return cursor -> {
            ParseResult<T> r1 = lhs.parse(cursor);
            ParseResult<U> r2 = rhs.parse(r1.getRemain());
            return new ParseResult<>(r2.getRemain(), new Pair<>(r1.getResult(), r2.getResult()));
        };
    }

    public static <T> Parser<T> or(Parser<T> lhs, Parser<T> rhs) {
        return cursor -> {
            try {
                return lhs.parse(cursor);
            } catch (ParseException e) {
                return rhs.parse(cursor);
            }
        };
    }

    public static <T> Parser<List<T>> oneOrMore(Parser<T> parser) {
        return cursor -> {
            ParseResult<T> r = parser.parse(cursor);
            List<T> list = new ArrayList<>();
            list.add(r.getResult());
            try {
                //noinspection InfiniteLoopStatement
                while (true) {
                    r = parser.parse(r.getRemain());
                    list.add(r.getResult());
                }
            } catch (ParseException e) {
                return new ParseResult<>(r.getRemain(), list);
            }
        };
    }

    public static <T> Parser<List<T>> zeroOrMore(Parser<T> parser) {
        return cursor -> {
            Cursor remain = cursor;
            List<T> list = new ArrayList<>();
            try {
                //noinspection InfiniteLoopStatement
                while (true) {
                    ParseResult<T> r = parser.parse(remain);
                    remain = r.getRemain();
                    list.add(r.getResult());
                }
            } catch (ParseException e) {
                return new ParseResult<>(remain, list);
            }
        };
    }

    public static <T, U> Parser<U> map(Parser<T> parser, Function<T, U> mapper) {
        return cursor -> {
            ParseResult<T> r = parser.parse(cursor);
            return new ParseResult<>(r.getRemain(), mapper.apply(r.getResult()));
        };
    }

    public static <T> Parser<T> optional(Parser<T> parser) {
        return cursor -> {
            try {
                return parser.parse(cursor);
            } catch (ParseException e) {
                return new ParseResult<>(cursor, null);
            }
        };
    }

    public static <T, U> Parser<U> skipFirst(Parser<T> lhs, Parser<U> rhs) {
        return cursor -> {
            Cursor remain = lhs.parse(cursor).getRemain();
            return rhs.parse(remain);
        };
    }

    public static <T, U> Parser<T> skipSecond(Parser<T> lhs, Parser<U> rhs) {
        return cursor -> {
            ParseResult<T> r1 = lhs.parse(cursor);
            Cursor remain = rhs.parse(r1.getRemain()).getRemain();
            return new ParseResult<>(remain, r1.getResult());
        };
    }

    public static <T> SkipWrapper<T> skip(Parser<T> parser) {
        return new SkipWrapper<>(parser);
    }

    public static <T, U> Parser<U> discard(Parser<T> parser) {
        return cursor -> {
            ParseResult<T> r = parser.parse(cursor);
            return new ParseResult<>(r.getRemain(), null);
        };
    }

    public static <T> Parser<T> callback(Parser<T> parser, Consumer<ParseResult<T>> onSuccess) {
        return cursor -> {
            ParseResult<T> r = parser.parse(cursor);
            onSuccess.accept(r);
            return r;
        };
    }

    public static <T, U> Parser<Pair<T, U>> bind(Parser<T> parser, Function<ParseResult<T>, Parser<U>> binder) {
        return cursor -> {
            ParseResult<T> r1 = parser.parse(cursor);
            ParseResult<U> r2 = binder.apply(r1).parse(r1.getRemain());
            return new ParseResult<>(r2.getRemain(), new Pair<>(r1.getResult(), r2.getResult()));
        };
    }
}
