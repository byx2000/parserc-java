package byx.parserc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Parser工厂类
 *
 * @author byx
 */
public class Parsers {
    /**
     * 匹配一个元素
     *
     * @param predicate 条件
     * @param <E> 元素类型
     * @return 匹配的元素
     */
    public static <E> Parser<E, E> one(Predicate<E> predicate) {
        return cursor -> {
            if (cursor.end()) {
                throw new ParseException(cursor);
            }
            E e = cursor.current();
            if (!predicate.test(e)) {
                throw new ParseException(cursor);
            }
            return ParseResult.of(cursor.next(), e);
        };
    }

    public static <E> Parser<E, E> one(E item) {
        return one(cur -> cur.equals(item));
    }

    public static <E> Parser<E, E> oneOf(Set<E> items) {
        return one(items::contains);
    }

    @SafeVarargs
    public static <E> Parser<E, E> oneOf(E... items) {
        return oneOf(Arrays.stream(items).collect(Collectors.toSet()));
    }

    public static <E> Parser<E, E> noneOf(Set<E> items) {
        return one(cur -> !items.contains(cur));
    }

    @SafeVarargs
    public static <E> Parser<E, E> noneOf(E... items) {
        return noneOf(Arrays.stream(items).collect(Collectors.toSet()));
    }

    public static Parser<Character, Character> range(char c1, char c2) {
        return one(c -> (c - c1) * (c - c2) <= 0);
    }

    public static <T, E> Parser<T, E> empty() {
        return cursor -> ParseResult.of(cursor, null);
    }

    public static <T, E> Parser<T, E> end() {
        return cursor -> {
            if (cursor.end()) {
                return ParseResult.of(cursor, null);
            }
            throw new ParseException(cursor);
        };
    }

    public static <T, U, E> Parser<Pair<T, U>, E> concat(Parser<T, E> lhs, Parser<U, E> rhs) {
        return cursor -> {
            ParseResult<T, E> r1 = lhs.parse(cursor);
            ParseResult<U, E> r2 = rhs.parse(r1.getRemain());
            return ParseResult.of(r2.getRemain(), Pair.of(r1.getResult(), r2.getResult()));
        };
    }

    public static <T, E> Parser<T, E> or(Parser<T, E> lhs, Parser<T, E> rhs) {
        return cursor -> {
            try {
                return lhs.parse(cursor);
            } catch (ParseException e) {
                return rhs.parse(cursor);
            }
        };
    }

    public static <T, E> Parser<List<T>, E> repeat(Parser<T, E> parser, int min, int max) {
        return cursor -> {
            int cnt = 0;
            List<T> results = new ArrayList<>();

            // 最少min次
            while (cnt != min) {
                ParseResult<T, E> r = parser.parse(cursor);
                results.add(r.getResult());
                cursor = r.getRemain();
                cnt++;
            }

            // 最多max次
            try {
                while (cnt != max) {
                    ParseResult<T, E> r = parser.parse(cursor);
                    results.add(r.getResult());
                    cursor = r.getRemain();
                    cnt++;
                }
            } catch (ParseException ignored) {}

            return ParseResult.of(cursor, results);
        };
    }

    public static <T, E> Parser<List<T>, E> zeroOrMore(Parser<T, E> parser) {
        return repeat(parser, 0, -1);
    }

    public static <T, E> Parser<List<T>, E> oneOrMore(Parser<T, E> parser) {
        return repeat(parser, 1, -1);
    }

    public static <T, U, E> Parser<U, E> map(Parser<T, E> parser, Function<T, U> mapper) {
        return cursor -> {
            ParseResult<T, E> r = parser.parse(cursor);
            return ParseResult.of(r.getRemain(), mapper.apply(r.getResult()));
        };
    }

    public static <T, U, E> Parser<U, E> skipFirst(Parser<T, E> lhs, Parser<U, E> rhs) {
        return concat(lhs, rhs).map(Pair::getSecond);
    }

    public static <T, U, E> Parser<T, E> skipSecond(Parser<T, E> lhs, Parser<U, E> rhs) {
        return concat(lhs, rhs).map(Pair::getFirst);
    }

    public static class SkipWrapper<T, E> {
        private final Parser<T, E> parser;

        private SkipWrapper(Parser<T, E> parser) {
            this.parser = parser;
        }

        public <U> Parser<U, E> concat(Parser<U, E> rhs) {
            return Parsers.skipFirst(parser, rhs);
        }
    }

    public static <T, U, E> SkipWrapper<T, E> skip(Parser<T, E> lhs) {
        return new SkipWrapper<>(lhs);
    }
}
