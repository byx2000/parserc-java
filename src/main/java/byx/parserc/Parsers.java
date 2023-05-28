package byx.parserc;

import byx.parserc.exception.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 常用解析器的静态工厂
 */
public class Parsers {
    /**
     * 在当前位置解析成功，并返回由resultSupplier生成的解析结果
     * @param resultSupplier 结果生成器，参数为cursor
     */
    public static <R> Parser<R> success(Function<Cursor, R> resultSupplier) {
        return cursor -> new ParseResult<>(resultSupplier.apply(cursor), cursor, cursor);
    }

    /**
     * 在当前位置解析成功，并返回由resultSupplier生成的解析结果
     * @param resultSupplier 结果生成器
     */
    public static <R> Parser<R> success(Supplier<R> resultSupplier) {
        return success(cursor -> resultSupplier.get());
    }

    /**
     * 在当前位置解析成功，并返回指定解析结果
     * @param result 结果
     */
    public static <R> Parser<R> success(R result) {
        return success(() -> result);
    }

    /**
     * 在当前位置解析成功，并返回null作为解析结果
     */
    public static <R> Parser<R> success() {
        return success((R) null);
    }

    /**
     * 在当前位置抛出自定义异常
     * @param exceptionSupplier 异常生成器，参数为cursor
     */
    public static <R> Parser<R> fail(Function<Cursor, RuntimeException> exceptionSupplier) {
        return cursor -> {
            throw exceptionSupplier.apply(cursor);
        };
    }

    /**
     * 在当前位置抛出自定义异常
     * @param exceptionSupplier 异常生成器
     */
    public static <R> Parser<R> fail(Supplier<RuntimeException> exceptionSupplier) {
        return fail(cursor -> exceptionSupplier.get());
    }

    /**
     * 在当前位置抛出ParseException，并携带错误消息msg
     * @param msg 错误消息
     */
    public static <R> Parser<R> fail(String msg) {
        return fail(cursor -> new ParseException(cursor, msg));
    }

    /**
     * 在当前位置抛出ParseException，错误消息为空
     */
    public static <R> Parser<R> fail() {
        return fail("");
    }

    /**
     * 不执行任何操作的解析器，并返回null作为解析结果
     */
    public static <R> Parser<R> empty() {
        return success();
    }

    /**
     * 不执行任何操作的解析器，并返回result作为解析结果
     * @param result 解析结果
     */
    public static <R> Parser<R> empty(R result) {
        return success(result);
    }

    /**
     * 如果当前位置到达输入末尾，则返回result作为解析结果，否则抛出ParseException
     * @param result 解析结果
     */
    public static <R> Parser<R> end(R result) {
        return cursor -> {
            if (!cursor.end()) {
                throw new ParseException(cursor, "expected end of input");
            }
            return new ParseResult<>(result, cursor, cursor);
        };
    }

    /**
     * 如果当前位置到达输入末尾，则返回null作为解析结果，否则抛出ParseException
     */
    public static <T> Parser<T> end() {
        return end(null);
    }

    /**
     * <p>如果当前位置的字符满足predicate指定的条件，则解析成功并将字符作为解析结果</p>
     * <p>如果predicate不满足或到达输入末尾，则抛出ParseException</p>
     * @param predicate 匹配条件
     */
    public static Parser<Character> ch(Predicate<Character> predicate) {
        return cursor -> {
            char c = cursor.current();
            if (predicate.test(c)) {
                return new ParseResult<>(c, cursor, cursor.next());
            }
            throw new ParseException(cursor);
        };
    }

    /**
     * <p>匹配当前位置的任何字符，并将当前位置的字符作为解析结果返回</p>
     * <p>如果到达输入末尾，则抛出ParseException</p>
     */
    public static Parser<Character> any() {
        return ch(c -> true);
    }

    /**
     * <p>如果当前位置的字符等于指定字符c，则解析成功，并返回c作为解析结果</p>
     * <p>如果当前位置的字符不等于c或到达输入末尾，则抛出ParseException</p>
     * @param c c
     */
    public static Parser<Character> ch(char c) {
        return ch(ch -> c == ch)
                .mapException((cursor, throwable) ->
                        new ParseException(cursor, String.format("expected character '%c'", c)));
    }

    /**
     * <p>如果当前位置的字符在区间[c1, c2]内，则解析成功，并返回当前位置的字符作为解析结果</p>
     * <p>如果当前位置的字符不在区间[c1, c2]内或到达输入末尾，则抛出ParseException</p>
     * @param c1 c1
     * @param c2 c2
     */
    public static Parser<Character> range(char c1, char c2) {
        return ch(c -> (c - c1) * (c - c2) <= 0)
                .mapException(((cursor, throwable) ->
                        new ParseException(cursor, String.format("expected character in range ['%c', '%c']", c1, c2))));
    }

    /**
     * <p>如果当前位置的字符在字符集chs内，则解析成功，并返回当前位置的字符作为解析结果</p>
     * <p>如果当前位置的字符不在字符集chs内或到达输入末尾，则抛出ParseException</p>
     * @param chs 字符集
     */
    public static Parser<Character> chs(Character... chs) {
        Set<Character> set = Arrays.stream(chs).collect(Collectors.toSet());
        return ch(set::contains).mapException((cursor, throwable) ->
                new ParseException(cursor, String.format("expected character in %s", set)));
    }

    /**
     * <p>如果当前位置的字符不在字符集chs内，则解析成功，并返回当前位置的字符作为解析结果</p>
     * <p>如果当前位置的字符在字符集chs内或到达输入末尾，则抛出ParseException</p>
     * @param chs 字符集
     */
    public static Parser<Character> not(Character... chs) {
        Set<Character> set = Arrays.stream(chs).collect(Collectors.toSet());
        return ch(c -> !set.contains(c)).mapException((cursor, throwable) ->
                new ParseException(cursor, String.format("expected character not in %s", set)));
    }

    /**
     * <p>如果当前位置以字符串s为前缀，则解析成功，并返回该字符串作为解析结果</p>
     * <p>如果前缀不匹配或在匹配过程中遇到输入结尾，则抛出ParseException</p>
     * @param s 字符串
     */
    public static Parser<String> str(String s) {
        return cursor -> {
            Cursor oldCursor = cursor;
            for (int i = 0; i < s.length(); ++i) {
                if (cursor.end()) {
                    throw new ParseException(oldCursor, String.format("expected %s", s));
                }
                if (cursor.current() != s.charAt(i)) {
                    throw new ParseException(oldCursor, String.format("expected %s", s));
                }
                cursor = cursor.next();
            }
            return new ParseResult<>(s, oldCursor, cursor);
        };
    }

    /**
     * <p>如果当前位置匹配ss中的任何字符串前缀，则解析成功，并返回该字符串作为解析结果</p>
     * <p>如果不匹配ss中的任何字符串或在匹配过程中遇到输入结尾，则抛出ParseException</p>
     * @param ss 字符串集合
     */
    public static Parser<String> strs(String... ss) {
        Set<String> set = Arrays.stream(ss).collect(Collectors.toSet());
        return Arrays.stream(ss).<Parser<String>>reduce(fail(), (p, s) -> p.or(str(s)), Parser::or)
                .mapException((cursor, throwable) ->
                        new ParseException(cursor, String.format("expected string in %s", set)));
    }

    /**
     * <p>连续应用多个解析器，并组合所有解析器的解析结果</p>
     * <p>如果任意一个解析器解析失败，则解析失败</p>
     * @param parsers 解析器数组
     */
    public static Parser<List<Object>> seq(Parser<?>... parsers) {
        return cursor -> {
            Cursor oldCursor = cursor;
            List<Object> result = new ArrayList<>();
            for (Parser<?> p : parsers) {
                ParseResult<?> r = p.parse(cursor);
                result.add(r.getResult());
                cursor = r.getRemain();
            }
            return new ParseResult<>(result, oldCursor, cursor);
        };
    }

    /**
     * <p>依次尝试应用parsers中的解析器，如果成功则返回其解析结果</p>
     * <p>如果所有解析器都解析失败，则解析失败</p>
     * @param parsers 解析器数组
     */
    @SafeVarargs
    public static <R> Parser<R> oneOf(Parser<R>... parsers) {
        return Arrays.stream(parsers).reduce(fail("no parser available"), Parser::or);
    }

    /**
     * <p>依次尝试应用parsers中的解析器，如果成功则返回其解析结果</p>
     * <p>parsers中的解析器可以是不同的类型</p>
     * <p>如果所有解析器都解析失败，则解析失败</p>
     * @param parsers 解析器数组
     */
    public static Parser<Object> alt(Parser<?>... parsers) {
        return input -> {
            for (Parser<?> p : parsers) {
                try {
                    return p.mapTo(Object.class).parse(input);
                } catch (ParseException ignored) {}
            }
            throw new ParseException(input, "no parser available");
        };
    }

    /**
     * <p>延迟解析器，解析动作发生时才调用parserSupplier获取解析器并调用其解析方法</p>
     * <p>该方法一般被用于解决解析器之间循环引用的问题</p>
     * @param parserSupplier 解析器生成器
     */
    public static <R> Parser<R> lazy(Supplier<Parser<R>> parserSupplier) {
        return cursor -> parserSupplier.get().parse(cursor);
    }

    public static class SkipWrapper<R> {
        private final Parser<R> lhs;

        public SkipWrapper(Parser<R> lhs) {
            this.lhs = lhs;
        }

        public <R2> Parser<R2> and(Parser<R2> rhs) {
            return lhs.and(rhs).map(Pair::getSecond);
        }
    }

    /**
     * 连接两个解析器，并丢弃第一个解析器的结果
     * @param lhs 第一个解析器
     */
    public static <R> SkipWrapper<R> skip(Parser<R> lhs) {
        return new SkipWrapper<>(lhs);
    }

    /**
     * 在当前位置应用解析器predicate，解析成功不消耗任何输入，解析失败抛出ParseException
     * @param predicate predicate
     */
    public static <R> Parser<R> expect(Parser<?> predicate) {
        return cursor -> {
            predicate.parse(cursor);
            return new ParseResult<>(null, cursor, cursor);
        };
    }

    /**
     * 在当前位置应用解析器predicate，解析成功抛出ParseException，解析失败不消耗任何输入
     * @param predicate predicate
     */
    public static <R> Parser<R> not(Parser<?> predicate) {
        return cursor -> {
            try {
                predicate.parse(cursor);
            } catch (ParseException e) {
                return new ParseResult<>(null, cursor, cursor);
            }
            throw new ParseException(cursor);
        };
    }
}
