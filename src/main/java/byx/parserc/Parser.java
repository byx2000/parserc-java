package byx.parserc;

import byx.parserc.exception.ParseInternalException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static byx.parserc.Parsers.chs;

/**
 * 解析器
 * @param <R> 解析结果类型
 */
public interface Parser<R> {
    /**
     * 解析字符串
     * @param s 字符串
     * @param index 当前位置
     * @return 解析结果
     */
    ParseResult<R> parse(String s, int index);

    /**
     * 解析字符串直到末尾
     * @param s 字符串
     * @return 解析结果
     */
    default R parse(String s) {
        ParseResult<R> r = parse(s, 0);
        if (r.index() != s.length()) {
            throw ParseInternalException.INSTANCE;
        }
        return r.result();
    }

    /**
     * <p>依次应用两个解析器，并组合两个解析器的解析结构</p>
     * <p>如果任意一个解析器解析失败，则解析失败</p>
     * @param rhs 解析器2
     */
    default <R2> Parser<Pair<R, R2>> and(Parser<R2> rhs) {
        return (s, index) -> {
            ParseResult<R> r1 = this.parse(s, index);
            ParseResult<R2> r2 = rhs.parse(s, r1.index());
            return new ParseResult<>(new Pair<>(r1.result(), r2.result()), r2.index());
        };
    }

    /**
     * 在当前解析器后连接字符c
     * @param c c
     */
    default Parser<Pair<R, Character>> and(char c) {
        return this.and(Parsers.ch(c));
    }

    /**
     * 在当前解析器后连接字符串s
     * @param s s
     */
    default Parser<Pair<R, String>> and(String s) {
        return this.and(Parsers.str(s));
    }

    /**
     * <p>依次尝试应用两个解析器，如果成功则返回其解析结果</p>
     * <p>如果两个解析器都失败，则解析失败</p>
     * @param rhs 解析器2
     */
    default Parser<R> or(Parser<R> rhs) {
        return (s, index) -> {
            try {
                return this.parse(s, index);
            } catch (ParseInternalException e) {
                return rhs.parse(s, index);
            }
        };
    }

    /**
     * 应用指定解析器，并转换解析结果
     * @param mapper 结果转换器
     */
    default <R2> Parser<R2> map(Function<R, R2> mapper) {
        return (s, index) -> {
            ParseResult<R> r = this.parse(s, index);
            return new ParseResult<>(mapper.apply(r.result()), r.index());
        };
    }

    /**
     * 丢弃当前解析器的结果，并返回另一个结果
     * @param result 结果
     */
    default <R2> Parser<R2> value(R2 result) {
        return this.map(r -> result);
    }

    /**
     * 连续应用当前解析器多次
     * @param minTimes 最小次数
     * @param maxTimes 最大次数
     */
    default Parser<List<R>> repeat(int minTimes, int maxTimes) {
        return (s, index) -> {
            List<R> result = new ArrayList<>();
            int times = 0;

            while (times < minTimes) {
                ParseResult<R> r = this.parse(s, index);
                result.add(r.result());
                index = r.index();
                times++;
            }

            while (times < maxTimes || maxTimes < 0) {
                try {
                    ParseResult<R> r = this.parse(s, index);
                    result.add(r.result());
                    index = r.index();
                    times++;
                } catch (ParseInternalException e) {
                    break;
                }
            }

            return new ParseResult<>(result, index);
        };
    }

    /**
     * 连续应用当前解析器指定次数
     * @param times 重复次数
     */
    default Parser<List<R>> repeat(int times) {
        return this.repeat(times, times);
    }

    /**
     * 连续应用当前解析器零次或多次，直到失败
     */
    default Parser<List<R>> many() {
        return this.repeat(0, -1);
    }

    /**
     * 连续应用当前解析器一次或多次，直到失败
     */
    default Parser<List<R>> many1() {
        return this.repeat(1, -1);
    }

    /**
     * 连接另一个解析器并跳过解析结果
     * @param rhs rhs
     */
    default <R2> Parser<R> skip(Parser<R2> rhs) {
        return this.and(rhs).map(Pair::first);
    }

    /**
     * 在解析器p前后连接prefix和suffix
     * @param prefix 前缀
     * @param suffix 后缀
     */
    default Parser<R> surround(Parser<?> prefix, Parser<?> suffix) {
        return Parsers.skip(prefix).and(this).skip(suffix);
    }

    /**
     * 在解析器p前后连接s
     * @param s s
     */
    default Parser<R> surround(Parser<?> s) {
        return this.surround(s, s);
    }

    /**
     * 在当前解析器前后加上空白符
     */
    default Parser<R> trim() {
        return this.surround(chs(' ', '\t', '\n', '\r').many());
    }

    /**
     * 解析器p解析成功时返回其解析结果，否则解析成功并返回defaultResult
     * @param defaultResult 默认值
     */
    default Parser<R> opt(R defaultResult) {
        return (s, index) -> {
            try {
                return this.parse(s, index);
            } catch (ParseInternalException e) {
                return new ParseResult<>(defaultResult, index);
            }
        };
    }

    /**
     * 首先应用当前解析器，然后调用mapper生成下一个解析器，再接着应用下一个解析器
     * @param mapper 解析器生成器
     */
    default <R2> Parser<Pair<R, R2>> flatMap(Function<ParseResult<R>, Parser<R2>> mapper) {
        return (s, index) -> {
            ParseResult<R> r1 = this.parse(s, index);
            ParseResult<R2> r2 = mapper.apply(r1).parse(s, r1.index());
            return new ParseResult<>(new Pair<>(r1.result(), r2.result()), r2.index());
        };
    }

    /**
     * 当前解析器抛出ParseException时，使用exceptionMapper转换异常并重新抛出
     * @param exceptionMapper 异常转换器
     */
    default Parser<R> fatal(BiFunction<String, Integer, RuntimeException> exceptionMapper) {
        return (s, index) -> {
            try {
                return parse(s, index);
            } catch (ParseInternalException e) {
                throw exceptionMapper.apply(s, index);
            }
        };
    }

    /**
     * 当前解析器抛出ParseException时，抛出特定异常
     * @param exceptionSupplier 异常生成器
     */
    default Parser<R> fatal(Supplier<RuntimeException> exceptionSupplier) {
        return fatal((s, i) -> exceptionSupplier.get());
    }
}
