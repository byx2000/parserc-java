package byx.parserc;

import byx.parserc.exception.FatalParseException;
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
 * 解析器组合子的静态工厂
 */
public class Parsers {
    /**
     * 抛出解析失败异常，并指定错误消息
     * @param msg 错误消息
     * @return Parser
     */
    public static <R> Parser<R> fail(String msg) {
        return input -> {
            throw new ParseException(input, msg);
        };
    }

    /**
     * 解析成功，并返回结果
     * @param result 结果
     * @return Parser
     */
    public static <R> Parser<R> success(R result) {
        return input -> new ParseResult<>(result, input);
    }

    /**
     * 匹配满足指定条件的字符，并指定错误消息
     * @param predicate 条件
     * @param errMsg 错误消息工厂
     * @return Parser
     */
    public static Parser<Character> satisfy(Predicate<Character> predicate, Supplier<String> errMsg) {
        return input -> {
            if (input.end()) {
                throw new ParseException(input, errMsg.get());
            }
            char c = input.current();
            if (!predicate.test(c)) {
                throw new ParseException(input, errMsg.get());
            }
            return new ParseResult<>(c, input.next());
        };
    }

    /**
     * 匹配任意字符
     * @return Parser
     */
    public static Parser<Character> any() {
        return satisfy(c -> true, () -> "");
    }

    /**
     * 匹配指定字符c
     * @param c c
     * @return Parser
     */
    public static Parser<Character> ch(char c) {
        return satisfy(ch -> c == ch, () -> String.format("expected %c", c));
    }

    /**
     * 匹配[c1, c2]范围内的字符
     * @param c1 c1
     * @param c2 c2
     * @return Parser
     */
    public static Parser<Character> range(char c1, char c2) {
        return satisfy(c -> (c - c1) * (c - c2) <= 0, () -> String.format("expected character in range [%c, %c]", c1, c2));
    }

    /**
     * 匹配chs集合内的字符
     * @param chs 字符集
     * @return Parser
     */
    public static Parser<Character> chs(Character... chs) {
        Set<Character> set = Arrays.stream(chs).collect(Collectors.toSet());
        return satisfy(set::contains, () -> String.format("expected character in set %s", set));
    }

    public static Parser<Character> not(char c) {
        return satisfy(ch -> ch != c, () -> String.format("unexpected character %c", c));
    }

    /**
     * 匹配字符串前缀s
     * @param s s
     * @return Parser
     */
    public static Parser<String> str(String s) {
        return input -> {
            Input oldInput = input;
            for (int i = 0; i < s.length(); ++i) {
                if (input.end()) {
                    throw new ParseException(input, String.format("expected %s", s));
                }
                if (input.current() != s.charAt(i)) {
                    throw new ParseException(oldInput, String.format("expected %s", s));
                }
                input = input.next();
            }
            return new ParseResult<>(s, input);
        };
    }

    /**
     * 匹配字符串前缀集合
     * @param s1 第一个字符串
     * @param s2 第二个字符串
     * @param ss 剩余字符串
     * @return Parser
     */
    public static Parser<String> strs(String s1, String s2, String... ss) {
        return Arrays.stream(ss).reduce(str(s1).or(str(s2)), (p, s) -> p.or(str(s)), Parser::or);
    }

    /**
     * 连接两个解析器，并组合两个解析器的解析结果
     * @param lhs 第一个解析器
     * @param rhs 第二个解析器
     * @return Parser
     */
    public static <R1, R2> Parser<Pair<R1, R2>> and(Parser<R1> lhs, Parser<R2> rhs) {
        return input -> {
            ParseResult<R1> r1 = lhs.parse(input);
            ParseResult<R2> r2 = rhs.parse(r1.getRemain());
            return new ParseResult<>(new Pair<>(r1.getResult(), r2.getResult()), r2.getRemain());
        };
    }

    /**
     * 连接多个解析器，并组合所有解析器的解析结果
     * @param parsers 解析器集合
     * @return Parser
     */
    public static Parser<List<Object>> seq(Parser<?>... parsers) {
        return input -> {
            List<Object> result = new ArrayList<>();
            for (Parser<?> p : parsers) {
                ParseResult<?> r = p.parse(input);
                result.add(r.getResult());
                input = r.getRemain();
            }
            return new ParseResult<>(result, input);
        };
    }

    /**
     * 有序选择两个解析器
     * @param lhs 第一个解析器
     * @param rhs 第二个解析器
     * @return Parser
     */
    public static <R> Parser<R> or(Parser<R> lhs, Parser<R> rhs) {
        return input -> {
            try {
                return lhs.parse(input);
            } catch (ParseException e) {
                return rhs.parse(input);
            }
        };
    }

    /**
     * 有序选择多个解析器
     * @param p1 第一个解析器
     * @param p2 第二个解析器
     * @param parsers 剩余解析器
     * @return Parser
     */
    @SafeVarargs
    public static <R> Parser<R> oneOf(Parser<R> p1, Parser<R> p2, Parser<R>... parsers) {
        return Arrays.stream(parsers).reduce(p1.or(p2), Parser::or);
    }

    /**
     * 转换指定解析器的解析结果
     * @param p 解析器
     * @param mapper 转换器
     * @return Parser
     */
    public static <R1, R2> Parser<R2> map(Parser<R1> p, Function<R1, R2> mapper) {
        return input -> {
            ParseResult<R1> r = p.parse(input);
            return new ParseResult<>(mapper.apply(r.getResult()), r.getRemain());
        };
    }

    /**
     * 连续应用指定解析器零次或多次
     * @param p 解析器
     * @return Parser
     */
    public static <R> Parser<List<R>> many(Parser<R> p) {
        return input -> {
            List<R> result = new ArrayList<>();
            try {
                while (true) {
                    ParseResult<R> r = p.parse(input);
                    result.add(r.getResult());
                    input = r.getRemain();
                }
            } catch (ParseException e) {
                return new ParseResult<>(result, input);
            }
        };
    }

    /**
     * 连续应用指定解析器一次或多次
     * @param p 解析器
     * @return Parser
     */
    public static <R> Parser<List<R>> many1(Parser<R> p) {
        return p.and(p.many()).map(r -> {
            List<R> result = new ArrayList<>();
            result.add(r.getFirst());
            result.addAll(r.getSecond());
            return result;
        });
    }

    /**
     * 让指定解析器变为可选，并在解析失败时返回默认值
     * @param p 解析器
     * @param defaultResult 默认值
     * @return Parser
     */
    public static <R> Parser<R> opt(Parser<R> p, R defaultResult) {
        return input -> {
            try {
                return p.parse(input);
            } catch (ParseException e) {
                return new ParseResult<>(defaultResult, input);
            }
        };
    }

    /**
     * 延迟解析器
     * @param supplier 解析器工厂
     * @return Parser
     */
    public static <R> Parser<R> lazy(Supplier<Parser<R>> supplier) {
        return input -> supplier.get().parse(input);
    }

    /**
     * 匹配被给定分隔符分隔的输入
     * @param delimiter 分隔符
     * @param parser 解析器
     * @return Parser
     */
    public static <R> Parser<List<R>> separate(Parser<?> delimiter, Parser<R> parser) {
        return parser.and(skip(delimiter).and(parser).many()).map(p -> {
            List<R> result = new ArrayList<>();
            result.add(p.getFirst());
            result.addAll(p.getSecond());
            return result;
        });
    }

    /**
     * skip(begin).and(p).skip(end)
     * @param p p
     * @param begin begin
     * @param end end
     * @return Parser
     */
    public static <R> Parser<R> surround(Parser<R> p, Parser<?> begin, Parser<?> end) {
        return skip(begin).and(p).skip(end);
    }

    /**
     * 连接两个解析器，并丢弃第一个解析器的结果
     * @param lhs 第一个解析器
     * @param rhs 第二个解析器
     * @return Parser
     */
    public static <R> Parser<R> skipFirst(Parser<?> lhs, Parser<R> rhs) {
        return lhs.and(rhs).map(Pair::getSecond);
    }

    /**
     * 连接两个解析器，并丢弃第二个解析器的结果
     * @param lhs 第一个解析器
     * @param rhs 第二个解析器
     * @return Parser
     */
    public static <R> Parser<R> skipSecond(Parser<R> lhs, Parser<?> rhs) {
        return lhs.and(rhs).map(Pair::getFirst);
    }

    public static class SkipWrapper<R> {
        private final Parser<R> lhs;

        public SkipWrapper(Parser<R> lhs) {
            this.lhs = lhs;
        }

        public <R2> Parser<R2> and(Parser<R2> rhs) {
            return skipFirst(lhs, rhs);
        }
    }

    /**
     * 跳过第一个解析器，并准备连接第二个解析器
     * @param lhs lhs
     * @return SkipWrapper
     */
    public static <R> SkipWrapper<R> skip(Parser<R> lhs) {
        return new SkipWrapper<>(lhs);
    }

    /**
     * 根据probe解析器的执行成功与否，选择执行success或failed解析器
     * @param probe probe
     * @param success success
     * @param failed failed
     * @return Parser
     */
    public static <R> Parser<R> peek(Parser<?> probe, Parser<R> success, Parser<R> failed) {
        return input -> {
            try {
                probe.parse(input);
            } catch (ParseException e) {
                return failed.parse(input);
            }
            return success.parse(input);
        };
    }

    /**
     * 多次应用当前解析器零次或多次，直到指定解析器失败
     * @param p p
     * @param until until
     * @return Parser
     */
    public static <R> Parser<List<R>> manyUntil(Parser<R> p, Parser<?> until) {
        return peek(until, fail(""), p).many();
    }

    /**
     * 首先应用第一个解析器，然后根据解析结果生成下一个解析器，再接着应用下一个解析器
     * @param p 解析器
     * @param flatMap 解析器生成器
     * @return Parser
     */
    public static <R1, R2> Parser<R2> then(Parser<R1> p, Function<R1, Parser<R2>> flatMap) {
        return input -> {
            ParseResult<R1> r = p.parse(input);
            return flatMap.apply(r.getResult()).parse(r.getRemain());
        };
    }

    /**
     * 当指定解析器解析失败时，抛出关键错误，该错误不会被or、oneOf等组合子捕获
     * @param p 解析器
     * @return Parser
     */
    public static <R> Parser<R> fatal(Parser<R> p) {
        return input -> {
            try {
                return p.parse(input);
            } catch (ParseException e) {
                throw new FatalParseException(e.getInput(), e.getMsg());
            }
        };
    }
}
