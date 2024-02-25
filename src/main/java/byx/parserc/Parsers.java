package byx.parserc;

import byx.parserc.exception.ParseInternalException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 常用解析器的静态工厂
 */
public class Parsers {
    /**
     * 在当前位置解析成功，并返回指定解析结果
     * @param result 结果
     */
    public static <R> Parser<R> success(R result) {
        return (s, index) -> new ParseResult<>(result, index);
    }

    /**
     * 在当前位置解析成功，并返回null作为解析结果
     */
    public static <R> Parser<R> success() {
        return success(null);
    }

    /**
     * 在当前位置抛出内部解析异常
     */
    public static <R> Parser<R> fail() {
        return (s, index) -> {
            throw ParseInternalException.INSTANCE;
        };
    }

    /**
     * 如果当前位置到达输入末尾，则返回null作为解析结果，否则抛出ParseException
     */
    public static <T> Parser<T> end() {
        return (s, index) -> {
            if (index != s.length()) {
                throw ParseInternalException.INSTANCE;
            }
            return new ParseResult<>(null, index);
        };
    }

    public static Parser<Character> ch(Predicate<Character> predicate) {
        return (s, index) -> {
            if (index < s.length() && predicate.test(s.charAt(index))) {
                return new ParseResult<>(s.charAt(index), index + 1);
            }
            throw ParseInternalException.INSTANCE;
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
        return ch(ch -> c == ch);
    }

    /**
     * <p>如果当前位置的字符在区间[c1, c2]内，则解析成功，并返回当前位置的字符作为解析结果</p>
     * <p>如果当前位置的字符不在区间[c1, c2]内或到达输入末尾，则抛出ParseException</p>
     * @param c1 c1
     * @param c2 c2
     */
    public static Parser<Character> range(char c1, char c2) {
        return ch(c -> (c - c1) * (c - c2) <= 0);
    }

    /**
     * <p>如果当前位置的字符在字符集chs内，则解析成功，并返回当前位置的字符作为解析结果</p>
     * <p>如果当前位置的字符不在字符集chs内或到达输入末尾，则抛出ParseException</p>
     * @param chs 字符集
     */
    public static Parser<Character> chs(Character... chs) {
        Set<Character> set = Arrays.stream(chs).collect(Collectors.toSet());
        return ch(set::contains);
    }

    /**
     * <p>如果当前位置的字符不在字符集chs内，则解析成功，并返回当前位置的字符作为解析结果</p>
     * <p>如果当前位置的字符在字符集chs内或到达输入末尾，则抛出ParseException</p>
     * @param chs 字符集
     */
    public static Parser<Character> not(Character... chs) {
        Set<Character> set = Arrays.stream(chs).collect(Collectors.toSet());
        return ch(c -> !set.contains(c));
    }

    /**
     * <p>如果当前位置以字符串s为前缀，则解析成功，并返回该字符串作为解析结果</p>
     * <p>如果前缀不匹配或在匹配过程中遇到输入结尾，则抛出ParseException</p>
     * @param str 字符串
     */
    public static Parser<String> str(String str) {
        return (s, index) -> {
            if (s.startsWith(str, index)) {
                return new ParseResult<>(str, index + str.length());
            } else {
                throw ParseInternalException.INSTANCE;
            }
        };
    }

    /**
     * <p>如果当前位置匹配ss中的任何字符串前缀，则解析成功，并返回该字符串作为解析结果</p>
     * <p>如果不匹配ss中的任何字符串或在匹配过程中遇到输入结尾，则抛出ParseException</p>
     * @param ss 字符串集合
     */
    public static Parser<String> strs(String... ss) {
        return (s, index) -> {
            for (String s1 : ss) {
                try {
                    return str(s1).parse(s, index);
                } catch (ParseInternalException ignored) {}
            }
            throw ParseInternalException.INSTANCE;
        };
    }

    /**
     * <p>连续应用多个解析器，并组合所有解析器的解析结果</p>
     * <p>如果任意一个解析器解析失败，则解析失败</p>
     * @param parsers 解析器数组
     */
    public static Parser<List<Object>> seq(Parser<?>... parsers) {
        return (s, index) -> {
            List<Object> result = new ArrayList<>();
            for (Parser<?> p : parsers) {
                ParseResult<?> r = p.parse(s, index);
                result.add(r.result());
                index = r.index();
            }
            return new ParseResult<>(result, index);
        };
    }

    /**
     * <p>依次尝试应用parsers中的解析器，如果成功则返回其解析结果</p>
     * <p>如果所有解析器都解析失败，则解析失败</p>
     * @param parsers 解析器数组
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <R> Parser<R> oneOf(Parser<? extends R>... parsers) {
        return (s, index) -> {
            for (Parser<? extends R> p : parsers) {
                try {
                    return (ParseResult<R>) p.parse(s, index);
                } catch (ParseInternalException ignored) {}
            }
            throw ParseInternalException.INSTANCE;
        };
    }

    /**
     * <p>延迟解析器，解析动作发生时才调用parserSupplier获取解析器并调用其解析方法</p>
     * <p>该方法一般被用于解决解析器之间循环引用的问题</p>
     * @param parserSupplier 解析器生成器
     */
    public static <R> Parser<R> lazy(Supplier<Parser<R>> parserSupplier) {
        return (s, index) -> parserSupplier.get().parse(s, index);
    }

    public static class SkipWrapper<R> {
        private final Parser<R> lhs;

        public SkipWrapper(Parser<R> lhs) {
            this.lhs = lhs;
        }

        public <R2> Parser<R2> and(Parser<R2> rhs) {
            return lhs.and(rhs).map(Pair::second);
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
        return (s, index) -> {
            predicate.parse(s, index);
            return new ParseResult<>(null, index);
        };
    }

    /**
     * 在当前位置应用解析器predicate，解析成功抛出ParseException，解析失败不消耗任何输入
     * @param predicate predicate
     */
    public static <R> Parser<R> not(Parser<?> predicate) {
        return (s, index) -> {
            try {
                predicate.parse(s, index);
            } catch (ParseInternalException e) {
                return new ParseResult<>(null, index);
            }
            throw ParseInternalException.INSTANCE;
        };
    }
}
