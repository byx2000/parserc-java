package byx.parserc;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Parser工厂类
 *
 * @author byx
 */
public class Parsers {
    public static <T, E> Parser<T, E> value(T val) {
        return cursor -> ParseResult.of(cursor, val);
    }

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
}
