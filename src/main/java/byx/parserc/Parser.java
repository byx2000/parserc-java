package byx.parserc;

import byx.parserc.exception.ParseException;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 解析器
 * @param <R> 解析结果类型
 */
public interface Parser<R> {
    /**
     * 解析输入
     * @param cursor 输入
     * @return 解析结果
     * @throws ParseException 解析异常
     */
    ParseResult<R> parse(Cursor cursor) throws ParseException;

    /**
     * 解析字符串
     * @param s 输入字符串
     * @return 解析结果
     * @throws ParseException 解析异常
     */
    default R parse(String s) throws ParseException {
        return parse(new Cursor(s, 0)).getResult();
    }

    /**
     * <p>依次应用两个解析器，并组合两个解析器的解析结构</p>
     * <p>如果任意一个解析器解析失败，则解析失败</p>
     * @param rhs 解析器2
     */
    default <R2> Parser<Pair<R, R2>> and(Parser<R2> rhs) {
        return Parsers.and(this, rhs);
    }

    /**
     * <p>依次尝试应用两个解析器，如果成功则返回其解析结果</p>
     * <p>如果两个解析器都失败，则解析失败</p>
     * @param rhs 解析器2
     */
    default Parser<R> or(Parser<R> rhs) {
        return Parsers.or(this, rhs);
    }

    /**
     * 应用指定解析器，并转换解析结果
     * @param mapper 结果转换器
     */
    default <R2> Parser<R2> map(Function<R, R2> mapper) {
        return Parsers.map(this, mapper);
    }

    /**
     * 应用解析器p，当发生异常时转换异常
     * @param exceptionMapper 异常转换器，参数为当前位置和异常对象
     */
    default Parser<R> mapException(BiFunction<Cursor, RuntimeException, RuntimeException> exceptionMapper) {
        return Parsers.mapException(this, exceptionMapper);
    }

    /**
     * 应用解析器p，当发生异常时转换异常
     * @param exceptionMapper 异常转换器，参数为异常对象
     */
    default Parser<R> mapException(Function<RuntimeException, RuntimeException> exceptionMapper) {
        return Parsers.mapException(this, exceptionMapper);
    }

    /**
     * 丢弃当前解析器的结果，并返回另一个结果
     * @param result 结果
     */
    default <R2> Parser<R2> map(R2 result) {
        return this.map(r -> result);
    }

    /**
     * 将当前解析器的结果强制转换为指定类型
     * @param type 类型
     */
    default <R2> Parser<R2> mapTo(Class<R2> type) {
        return this.map(type::cast);
    }

    /**
     * 连续应用当前解析器零次或多次，直到失败
     */
    default Parser<List<R>> many() {
        return Parsers.many(this);
    }

    /**
     * 连续应用当前解析器一次或多次，直到失败
     */
    default Parser<List<R>> many1() {
        return Parsers.many1(this);
    }

    /**
     * 连续应用当前解析器指定次数
     * @param times 重复次数
     */
    default Parser<List<R>> repeat(int times) {
        return Parsers.repeat(this, times);
    }

    /**
     * 连接另一个解析器并跳过解析结果
     * @param rhs rhs
     */
    default <R2> Parser<R> skip(Parser<R2> rhs) {
        return Parsers.skipSecond(this, rhs);
    }

    /**
     * 在解析器p前后连接prefix和suffix
     * @param prefix 前缀
     * @param suffix 后缀
     */
    default Parser<R> surround(Parser<?> prefix, Parser<?> suffix) {
        return Parsers.surround(this, prefix, suffix);
    }

    /**
     * 在解析器p前后连接s
     * @param s s
     */
    default Parser<R> surround(Parser<?> s) {
        return Parsers.surround(this, s);
    }

    /**
     * 解析器p解析成功时返回其解析结果，否则解析成功并返回defaultResult
     * @param defaultResult 默认值
     */
    default Parser<R> opt(R defaultResult) {
        return Parsers.opt(this, defaultResult);
    }

    /**
     * 解析器p解析成功时返回其解析结果，否则解析成功并返回null
     */
    default Parser<R> opt() {
        return Parsers.opt(this);
    }

    /**
     * 首先应用当前解析器，然后调用flatMap生成下一个解析器，再接着应用下一个解析器
     * @param flatMap 解析器生成器
     */
    default <R2> Parser<R2> then(Function<ParseResult<R>, Parser<R2>> flatMap) {
        return Parsers.then(this, flatMap);
    }

    /**
     * <p>当前解析器抛出ParseException时，转化成FatalParseException重新抛出，并携带错误消息msg</p>
     * <p>FatalParseException不会被or和oneOf等组合子捕获</p>
     * @param msg msg
     */
    default Parser<R> fatal(String msg) {
        return Parsers.fatal(this, msg);
    }

    /**
     * 当解析器p抛出ParseException时，转化成exceptionSupplier生成的自定义异常并重新抛出
     * @param exceptionSupplier 异常生成器
     */
    default Parser<R> fatal(Function<Cursor, RuntimeException> exceptionSupplier) {
        return Parsers.fatal(this, exceptionSupplier);
    }

    /**
     * 如果当前位置到达输入末尾，则返回null作为解析结果，否则抛出ParseException
     */
    default Parser<R> end() {
        return this.skip(Parsers.end());
    }
}
