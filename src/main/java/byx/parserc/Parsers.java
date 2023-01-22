package byx.parserc;

import byx.parserc.exception.FatalParseException;
import byx.parserc.exception.ParseException;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 解析器组合子的静态工厂
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
     * <p>连续应用两个解析器，并组合两个解析器的解析结果</p>
     * <p>如果任意一个解析器解析失败，则解析失败</p>
     * @param lhs 解析器1
     * @param rhs 解析器2
     */
    public static <R1, R2> Parser<Pair<R1, R2>> and(Parser<R1> lhs, Parser<R2> rhs) {
        return cursor -> {
            ParseResult<R1> r1 = lhs.parse(cursor);
            ParseResult<R2> r2 = rhs.parse(r1.getRemain());
            return new ParseResult<>(new Pair<>(r1.getResult(), r2.getResult()), cursor, r2.getRemain());
        };
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
     * <p>依次尝试应用两个解析器，如果成功则返回其解析结果</p>
     * <p>如果两个解析器都失败，则解析失败</p>
     * @param lhs 解析器1
     * @param rhs 解析器2
     */
    public static <R> Parser<R> or(Parser<R> lhs, Parser<R> rhs) {
        return cursor -> {
            try {
                return lhs.parse(cursor);
            } catch (ParseException e) {
                return rhs.parse(cursor);
            }
        };
    }

    /**
     * <p>依次尝试应用parsers中的解析器，如果成功则返回其解析结果</p>
     * <p>如果所有解析器都解析失败，则解析失败</p>
     * @param parsers 解析器数组
     */
    @SafeVarargs
    public static <R> Parser<R> oneOf(Parser<R>... parsers) {
        return Arrays.stream(parsers).reduce(fail(), Parser::or);
    }

    /**
     * 应用解析器p，并转换解析结果
     * @param p 解析器
     * @param mapper 结果转换器
     */
    public static <R1, R2> Parser<R2> map(Parser<R1> p, Function<R1, R2> mapper) {
        return cursor -> {
            ParseResult<R1> r = p.parse(cursor);
            return new ParseResult<>(mapper.apply(r.getResult()), cursor, r.getRemain());
        };
    }

    /**
     * 应用解析器p，当发生异常时转换异常
     * @param p 解析器
     * @param exceptionMapper 异常转换器，参数为当前位置和异常对象
     */
    public static <R> Parser<R> mapException(Parser<R> p, BiFunction<Cursor, RuntimeException, RuntimeException> exceptionMapper) {
        return cursor -> {
            try {
                return p.parse(cursor);
            } catch (RuntimeException t) {
                throw exceptionMapper.apply(cursor, t);
            }
        };
    }

    /**
     * 应用解析器p，当发生异常时转换异常
     * @param p 解析器
     * @param exceptionMapper 异常转换器，参数为异常对象
     */
    public static <R> Parser<R> mapException(Parser<R> p, Function<RuntimeException, RuntimeException> exceptionMapper) {
        return mapException(p, ((cursor, throwable) -> exceptionMapper.apply(throwable)));
    }

    /**
     * 连续应用解析器p零次或多次，直到失败
     * @param p 解析器
     */
    public static <R> Parser<List<R>> many(Parser<R> p) {
        return cursor -> {
            Cursor oldCursor = cursor;
            List<R> result = new ArrayList<>();
            try {
                while (true) {
                    ParseResult<R> r = p.parse(cursor);
                    result.add(r.getResult());
                    cursor = r.getRemain();
                }
            } catch (ParseException e) {
                return new ParseResult<>(result, oldCursor, cursor);
            }
        };
    }

    /**
     * 连续应用解析器p一次或多次，直到失败
     * @param p 解析器
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
     * 连续应用解析器p指定次数
     * @param p 解析器
     * @param times 重复次数
     */
    public static <R> Parser<List<R>> repeat(Parser<R> p, int times) {
        return cursor -> {
            Cursor oldCursor = cursor;
            List<R> result = new ArrayList<>();
            for (int i = 0; i < times; i++) {
                ParseResult<R> r = p.parse(cursor);
                result.add(r.getResult());
                cursor = r.getRemain();
            }
            return new ParseResult<>(result, oldCursor, cursor);
        };
    }

    /**
     * 解析器p解析成功时返回其解析结果，否则解析成功并返回defaultResult
     * @param p 解析器
     * @param defaultResult 默认值
     */
    public static <R> Parser<R> opt(Parser<R> p, R defaultResult) {
        return cursor -> {
            try {
                return p.parse(cursor);
            } catch (ParseException e) {
                return new ParseResult<>(defaultResult, cursor, cursor);
            }
        };
    }

    /**
     * 解析器p解析成功时返回其解析结果，否则解析成功并返回null
     * @param p 解析器
     */
    public static <R> Parser<R> opt(Parser<R> p) {
        return opt(p, null);
    }

    /**
     * <p>延迟解析器，解析动作发生时才调用parserSupplier获取解析器并调用其解析方法</p>
     * <p>该方法一般被用于解决解析器之间循环引用的问题</p>
     * @param parserSupplier 解析器生成器
     */
    public static <R> Parser<R> lazy(Supplier<Parser<R>> parserSupplier) {
        return cursor -> parserSupplier.get().parse(cursor);
    }

    /**
     * <p>解析列表型数据</p>
     * <p>prefix elem delimiter elem delimiter ... suffix</p>
     * @param prefix 前缀
     * @param suffix 后缀
     * @param delimiter 元素分隔符
     * @param elem 元素
     */
    public static <R> Parser<List<R>> list(Parser<?> prefix, Parser<?> suffix, Parser<?> delimiter, Parser<R> elem) {
        Parser<List<R>> emptyList = prefix.and(suffix).map(r -> Collections.emptyList());
        Parser<List<R>> notEmptyList = skip(prefix).and(elem).and(skip(delimiter).and(elem).many()).skip(suffix).map(p -> {
            List<R> result = new ArrayList<>();
            result.add(p.getFirst());
            result.addAll(p.getSecond());
            return result;
        });
        return oneOf(notEmptyList, emptyList);
    }

    /**
     * <p>解析列表型数据</p>
     * <p>elem delimiter elem delimiter ...</p>
     * @param delimiter 元素分隔符
     * @param elem 元素
     */
    public static <R> Parser<List<R>> list(Parser<?> delimiter, Parser<R> elem) {
        return list(empty(), empty(), delimiter, elem);
    }

    /**
     * 在解析器p前后连接prefix和suffix
     * @param p 解析器
     * @param prefix 前缀
     * @param suffix 后缀
     */
    public static <R> Parser<R> surround(Parser<R> p, Parser<?> prefix, Parser<?> suffix) {
        return skip(prefix).and(p).skip(suffix);
    }

    /**
     * 在解析器p前后连接s
     * @param p p
     * @param s s
     */
    public static <R> Parser<R> surround(Parser<R> p, Parser<?> s) {
        return surround(p, s, s);
    }

    /**
     * 连接两个解析器，并丢弃第一个解析器的结果
     * @param lhs 第一个解析器
     * @param rhs 第二个解析器
     */
    public static <R> Parser<R> skipFirst(Parser<?> lhs, Parser<R> rhs) {
        return lhs.and(rhs).map(Pair::getSecond);
    }

    /**
     * 连接两个解析器，并丢弃第二个解析器的结果
     * @param lhs 第一个解析器
     * @param rhs 第二个解析器
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
     * 跳过第一个解析器，并连接第二个解析器
     * @param lhs 第一个解析器
     */
    public static <R> SkipWrapper<R> skip(Parser<R> lhs) {
        return new SkipWrapper<>(lhs);
    }

    /**
     * 根据predicate解析器的执行成功与否，选择执行success或failed解析器
     * @param predicate predicate
     * @param success success
     * @param failed failed
     */
    public static <R> Parser<R> peek(Parser<?> predicate, Parser<R> success, Parser<R> failed) {
        return cursor -> {
            try {
                predicate.parse(cursor);
            } catch (ParseException e) {
                return failed.parse(cursor);
            }
            return success.parse(cursor);
        };
    }

    /**
     * 一直向前解析，直到解析器p执行成功，并返回p的解析结果
     * @param p p
     */
    public static <R> Parser<R> until(Parser<R> p) {
        return skip(peek(p, fail(), any()).many()).and(p);
    }

    /**
     * 首先应用解析器p，然后调用flatMap生成下一个解析器，再接着应用下一个解析器
     * @param p 解析器
     * @param flatMap 解析器生成器
     */
    public static <R1, R2> Parser<R2> then(Parser<R1> p, Function<ParseResult<R1>, Parser<R2>> flatMap) {
        return cursor -> {
            ParseResult<R1> r = p.parse(cursor);
            return flatMap.apply(r).parse(r.getRemain());
        };
    }

    /**
     * 当解析器p抛出ParseException时，使用exceptionMapper转换异常并重新抛出
     * @param p 解析器p
     * @param exceptionMapper 异常转换器
     */
    public static <R> Parser<R> fatal(Parser<R> p, BiFunction<Cursor, ParseException, RuntimeException> exceptionMapper) {
        return p.mapException((cursor, e) -> {
            if (e instanceof ParseException) {
                return exceptionMapper.apply(cursor, (ParseException) e);
            }
            return e;
        });
    }

    /**
     * 当当解析器p抛出ParseException时，使用exceptionMapper转换异常并重新抛出
     * @param p 解析器p
     * @param exceptionMapper 异常转换器
     */
    public static <R> Parser<R> fatal(Parser<R> p, Function<Cursor, RuntimeException> exceptionMapper) {
        return fatal(p, (c, e) -> exceptionMapper.apply(c));
    }

    /**
     * <p>当解析器p抛出ParseException时，转化成FatalParseException重新抛出，并携带错误消息msg</p>
     * <p>FatalParseException不会被or和oneOf组合子捕获</p>
     * @param p 解析器p
     * @param msg 错误消息
     */
    public static <R> Parser<R> fatal(Parser<R> p, String msg) {
        return fatal(p, c -> new FatalParseException(c, msg));
    }

    /**
     * <p>当解析器p抛出ParseException时，转化成FatalParseException重新抛出，并携带ParseException的错误消息</p>
     * @param p 解析器p
     */
    public static <R> Parser<R> fatal(Parser<R> p) {
        return fatal(p, (c, e) -> new FatalParseException(c, e.getMsg()));
    }
}
