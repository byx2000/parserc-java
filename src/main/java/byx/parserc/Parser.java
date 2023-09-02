package byx.parserc;

import byx.parserc.exception.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static byx.parserc.Parsers.chs;

/**
 * 解析器
 * @param <R> 解析结果类型
 */
public interface Parser<R> {
    /**
     * 解析输入
     * @param cursor 输入
     * @return 解析结果
     */
    ParseResult<R> parse(Cursor cursor);

    /**
     * 解析字符串
     * @param s 输入字符串
     * @return 解析结果
     */
    default R parse(String s) {
        return parse(new Cursor(s)).getResult();
    }

    /**
     * <p>依次应用两个解析器，并组合两个解析器的解析结构</p>
     * <p>如果任意一个解析器解析失败，则解析失败</p>
     * @param rhs 解析器2
     */
    default <R2> Parser<Pair<R, R2>> and(Parser<R2> rhs) {
        return cursor -> {
            ParseResult<R> r1 = this.parse(cursor);
            ParseResult<R2> r2 = rhs.parse(r1.getRemain());
            return new ParseResult<>(new Pair<>(r1.getResult(), r2.getResult()), cursor, r2.getRemain());
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
        return cursor -> {
            try {
                return this.parse(cursor);
            } catch (ParseException e) {
                return rhs.parse(cursor);
            }
        };
    }

    /**
     * 应用指定解析器，并转换解析结果
     * @param mapper 结果转换器
     */
    default <R2> Parser<R2> map(Function<R, R2> mapper) {
        return cursor -> {
            ParseResult<R> r = this.parse(cursor);
            return new ParseResult<>(mapper.apply(r.getResult()), cursor, r.getRemain());
        };
    }

    /**
     * 应用解析器p，当发生异常时转换异常
     * @param exceptionMapper 异常转换器，参数为当前位置和异常对象
     */
    default Parser<R> mapException(BiFunction<Cursor, RuntimeException, RuntimeException> exceptionMapper) {
        return cursor -> {
            try {
                return this.parse(cursor);
            } catch (RuntimeException t) {
                throw exceptionMapper.apply(cursor, t);
            }
        };
    }

    /**
     * 应用解析器p，当发生异常时转换异常
     * @param exceptionMapper 异常转换器，参数为异常对象
     */
    default Parser<R> mapException(Function<RuntimeException, RuntimeException> exceptionMapper) {
        return this.mapException((cursor, throwable) -> exceptionMapper.apply(throwable));
    }

    /**
     * 丢弃当前解析器的结果，并返回另一个结果
     * @param result 结果
     */
    default <R2> Parser<R2> value(R2 result) {
        return this.map(r -> result);
    }

    /**
     * 连续应用当前解析器零次或多次，直到失败
     */
    default Parser<List<R>> many() {
        return cursor -> {
            Cursor oldCursor = cursor;
            List<R> result = new ArrayList<>();

            while (true) {
                try {
                    ParseResult<R> r = this.parse(cursor);
                    result.add(r.getResult());
                    cursor = r.getRemain();
                } catch (ParseException e) {
                    break;
                }
            }

            return new ParseResult<>(result, oldCursor, cursor);
        };
    }

    /**
     * 连续应用当前解析器一次或多次，直到失败
     */
    default Parser<List<R>> many1() {
        return this.and(this.many()).map(r -> {
            List<R> result = new ArrayList<>();
            result.add(r.getFirst());
            result.addAll(r.getSecond());
            return result;
        });
    }

    /**
     * 连续应用当前解析器指定次数
     * @param times 重复次数
     */
    default Parser<List<R>> repeat(int times) {
        return cursor -> {
            Cursor oldCursor = cursor;
            List<R> result = new ArrayList<>();
            for (int i = 0; i < times; i++) {
                ParseResult<R> r = this.parse(cursor);
                result.add(r.getResult());
                cursor = r.getRemain();
            }
            return new ParseResult<>(result, oldCursor, cursor);
        };
    }

    /**
     * 连接另一个解析器并跳过解析结果
     * @param rhs rhs
     */
    default <R2> Parser<R> skip(Parser<R2> rhs) {
        return this.and(rhs).map(Pair::getFirst);
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
     * 解析器p解析成功时返回其解析结果，否则解析成功并返回defaultResult
     * @param defaultResult 默认值
     */
    default Parser<R> opt(R defaultResult) {
        return cursor -> {
            try {
                return this.parse(cursor);
            } catch (ParseException e) {
                return new ParseResult<>(defaultResult, cursor, cursor);
            }
        };
    }

    /**
     * 解析器p解析成功时返回其解析结果，否则解析成功并返回null
     */
    default Parser<R> opt() {
        return this.opt(null);
    }

    /**
     * 首先应用当前解析器，然后调用mapper生成下一个解析器，再接着应用下一个解析器
     * @param mapper 解析器生成器
     */
    default <R2> Parser<Pair<R, R2>> then(Function<ParseResult<R>, Parser<R2>> mapper) {
        return cursor -> {
            ParseResult<R> r1 = this.parse(cursor);
            ParseResult<R2> r2 = mapper.apply(r1).parse(r1.getRemain());
            return new ParseResult<>(new Pair<>(r1.getResult(), r2.getResult()), cursor, r2.getRemain());
        };
    }

    /**
     * 当前解析器抛出ParseException时，使用exceptionMapper转换异常并重新抛出
     * @param exceptionMapper 异常转换器
     */
    default Parser<R> fatal(BiFunction<Cursor, ParseException, RuntimeException> exceptionMapper) {
        return this.mapException((cursor, e) -> {
            if (e instanceof ParseException) {
                return exceptionMapper.apply(cursor, (ParseException) e);
            }
            return e;
        });
    }

    /**
     * 当前解析器抛出ParseException时，使用exceptionMapper转换异常并重新抛出
     * @param exceptionMapper 异常转换器
     */
    default Parser<R> fatal(Function<Cursor, RuntimeException> exceptionMapper) {
        return this.fatal((c, e) -> exceptionMapper.apply(c));
    }

    /**
     * 如果当前位置到达输入末尾，则返回null作为解析结果，否则抛出ParseException
     */
    default Parser<R> end() {
        return this.skip(Parsers.end());
    }

    /**
     * 当前解析器应用后执行断言predicate，断言失败抛出异常
     * @param predicate predicate
     */
    default Parser<R> follow(Parser<?> predicate) {
        return this.skip(Parsers.expect(predicate));
    }

    /**
     * 当前解析器应用后执行断言predicate，断言成功抛出异常
     * @param predicate predicate
     */
    default Parser<R> notFollow(Parser<?> predicate) {
        return this.skip(Parsers.not(predicate));
    }

    /**
     * 在当前解析器前后加上空白符
     */
    default Parser<R> trim() {
        return this.surround(chs(' ', '\t', '\n', '\r').many());
    }
}
