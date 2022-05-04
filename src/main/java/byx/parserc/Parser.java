package byx.parserc;

import byx.parserc.exception.ParseException;

import java.util.List;
import java.util.function.Function;

/**
 * 解析器
 * @param <R> 解析结果类型
 */
public interface Parser<R> {
    /**
     * 解析输入
     * @param input 输入
     * @return 解析结果
     * @throws ParseException 解析异常
     */
    ParseResult<R> parse(Input input) throws ParseException;

    /**
     * 解析字符串
     * @param s 输入字符串
     * @return 解析结果
     * @throws ParseException 解析异常
     */
    default R parse(String s) throws ParseException {
        ParseResult<R> r = parse(new Input(s, 0));
        if (!r.getRemain().end()) {
            throw new ParseException(r.getRemain(), "end of file not reached");
        }
        return r.getResult();
    }

    /**
     * 连接另一个解析器
     * @param rhs rhs
     * @return Parser
     */
    default <R2> Parser<Pair<R, R2>> and(Parser<R2> rhs) {
        return Parsers.and(this, rhs);
    }

    /**
     * 有序选择另一个解析器
     * @param rhs rhs
     * @return Parser
     */
    default Parser<R> or(Parser<R> rhs) {
        return Parsers.or(this, rhs);
    }

    /**
     * 转换当前解析器的解析结果
     * @param mapper 结果转换器
     * @return Parser
     */
    default <R2> Parser<R2> map(Function<R, R2> mapper) {
        return Parsers.map(this, mapper);
    }

    /**
     * 丢弃当前解析器的结果，并返回另一个结果
     * @param result 结果
     * @return Parser
     */
    default <R2> Parser<R2> map(R2 result) {
        return this.map(r -> result);
    }

    /**
     * 将当前解析器的结果强制转换为指定类型
     * @param type 类型
     * @return Parser
     */
    default <R2> Parser<R2> mapTo(Class<R2> type) {
        return this.map(type::cast);
    }

    /**
     * 连续应用当前解析器零次或多次
     * @return Parser
     */
    default Parser<List<R>> many() {
        return Parsers.many(this);
    }

    /**
     * 连续应用当前解析器一次或多次
     * @return Parser
     */
    default Parser<List<R>> many1() {
        return Parsers.many1(this);
    }

    /**
     * 连接另一个解析器并跳过解析结果
     * @param rhs rhs
     * @return Parser
     */
    default <R2> Parser<R> skip(Parser<R2> rhs) {
        return Parsers.skipSecond(this, rhs);
    }

    /**
     * skip(begin).and(this).skip(end)
     * @param begin begin
     * @param end end
     * @return Parser
     */
    default Parser<R> surround(Parser<?> begin, Parser<?> end) {
        return Parsers.surround(this, begin, end);
    }

    /**
     * skip(p).and(this).skip(p)
     * @param p p
     * @return Parser
     */
    default Parser<R> surround(Parser<?> p) {
        return surround(p, p);
    }

    /**
     * 将当前解析器变为可选，并提供默认值
     * @param defaultResult 默认值
     * @return Parser
     */
    default Parser<R> opt(R defaultResult) {
        return Parsers.opt(this, defaultResult);
    }

    /**
     * 多次应用当前解析器零次或多次，直到指定解析器失败
     * @param until until
     * @return Parser
     */
    default Parser<List<R>> manyUntil(Parser<?> until) {
        return Parsers.manyUntil(this, until);
    }

    /**
     * 使用当前解析器的解析结果生成第二个解析器，并应用第二个解析器
     * @param flatMap 解析器生成器
     * @return Parser
     */
    default <R2> Parser<R2> then(Function<R, Parser<R2>> flatMap) {
        return Parsers.then(this, flatMap);
    }

    /**
     * 当前解析器出错时，抛出关键错误
     * @return Parser
     */
    default Parser<R> fatal() {
        return Parsers.fatal(this);
    }
}
