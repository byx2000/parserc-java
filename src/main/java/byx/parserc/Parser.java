package byx.parserc;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

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
            throw new ParseException(r.getRemain());
        }
        return r.getResult();
    }

    default <R2> Parser<Pair<R, R2>> and(Parser<R2> rhs) {
        return Parsers.and(this, rhs);
    }

    default Parser<R> or(Parser<R> rhs) {
        return Parsers.or(this, rhs);
    }

    default <R2> Parser<R2> map(Function<R, R2> mapper) {
        return Parsers.map(this, mapper);
    }

    default <R2> Parser<R2> map(Supplier<R2> supplier) {
        return Parsers.map(this, r -> supplier.get());
    }

    default <R2> Parser<R2> mapTo(Class<R2> type) {
        return this.map(type::cast);
    }

    default Parser<List<R>> many() {
        return Parsers.many(this);
    }

    default Parser<List<R>> many1() {
        return Parsers.many1(this);
    }

    default <R2> Parser<R> skip(Parser<R2> rhs) {
        return Parsers.skipSecond(this, rhs);
    }

    default Parser<R> surroundBy(Parser<?> begin, Parser<?> end) {
        return Parsers.skip(begin).and(this).skip(end);
    }

    default Parser<R> surroundBy(Parser<?> p) {
        return surroundBy(p, p);
    }
}
