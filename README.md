# parserc-java

parserc-java是用java实现的解析器组合子（Parser Combinator）库，可以方便地以自底向上的方式构建复杂的解析器。

## 计算器示例

```java
/**
 * 表达式计算器
 */
class ExprCalc {
    private static final Parser<?> digit = range('0', '9');
    private static final Parser<Character> add = ch('+').trim();
    private static final Parser<Character> sub = ch('-').trim();
    private static final Parser<Character> mul = ch('*').trim();
    private static final Parser<Character> div = ch('/').trim();
    private static final Parser<Character> lp = ch('(').trim();
    private static final Parser<Character> rp = ch(')').trim();
    private static final Parser<String> digits = digit.many1().map(ExprCalc::join);
    private static final Parser<Double> integer = digits.map(Double::parseDouble);
    private static final Parser<Double> decimal = seq(digits, ch('.'), digits).map(ExprCalc::join).map(Double::parseDouble);
    private static final Parser<Double> number = decimal.or(integer).trim();
    private static final Parser<Double> bracketExpr = skip(lp).and(lazy(() -> ExprCalc.expr)).skip(rp);
    private static final Parser<Double> negFact = skip(sub).and(lazy(() -> ExprCalc.fact)).map(e -> -e);
    private static final Parser<Double> fact = oneOf(number, bracketExpr, negFact);
    private static final Parser<Double> term = fact.and(mul.or(div).and(fact).many()).map(ExprCalc::calc);
    private static final Parser<Double> expr = term.and(add.or(sub).and(term).many()).map(ExprCalc::calc);
    private static final Parser<Double> parser = expr.end();

    private static String join(List<?> list) {
        return list.stream().map(Objects::toString).collect(Collectors.joining());
    }

    private static Double calc(Pair<Double, List<Pair<Character, Double>>> p) {
        double res = p.getFirst();
        for (Pair<Character, Double> pp : p.getSecond()) {
            switch (pp.getFirst()) {
                case '+':
                    res += pp.getSecond();
                    break;
                case '-':
                    res -= pp.getSecond();
                    break;
                case '*':
                    res *= pp.getSecond();
                    break;
                case '/':
                    res /= pp.getSecond();
                    break;
            }
        }
        return res;
    }

    public static Double eval(String input) {
        return parser.parse(input);
    }
}
```

## JSON示例

```java
/**
 * json解析器
 */
class JsonParser {
    private static final Parser<String> digit = range('0', '9').map(Objects::toString);
    private static final Parser<String> digits = digit.many1().map(JsonParser::join);
    private static final Parser<Integer> integer = digits.map(Integer::parseInt).trim();
    private static final Parser<Double> decimal = seq(digits, ch('.'), digits).map(JsonParser::join).map(Double::parseDouble);
    private static final Parser<String> string = skip(ch('"')).and(not('"').many()).skip(ch('"')).map(JsonParser::join);
    private static final Parser<Boolean> bool = strs("true", "false").map(Boolean::parseBoolean).trim();
    private static final Parser<Character> objStart = ch('{').trim();
    private static final Parser<Character> objEnd = ch('}').trim();
    private static final Parser<Character> arrStart = ch('[').trim();
    private static final Parser<Character> arrEnd = ch(']').trim();
    private static final Parser<Character> colon = ch(':').trim();
    private static final Parser<Character> comma = ch(',').trim();
    private static final Parser<Object> lazyJsonObj = lazy(() -> JsonParser.jsonObj);
    private static final Parser<List<Object>> jsonObjList = lazyJsonObj.and(skip(comma).and(lazyJsonObj).many())
        .map(r -> reduceList(r.getFirst(), r.getSecond()));
    private static final Parser<List<Object>> arr = skip(arrStart).and(jsonObjList.opt(Collections.emptyList())).skip(arrEnd);
    private static final Parser<Pair<String, Object>> pair = string.skip(colon).and(lazyJsonObj);
    private static final Parser<List<Pair<String, Object>>> pairList = pair.and(skip(comma).and(pair).many())
        .map(r -> reduceList(r.getFirst(), r.getSecond()));
    private static final Parser<Map<String, Object>> obj = skip(objStart).and(pairList.opt(Collections.emptyList())).skip(objEnd)
        .map(ps -> ps.stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
    private static final Parser<Object> jsonObj = oneOf(decimal, integer, string, bool, arr, obj);
    private static final Parser<Object> parser = jsonObj.end();

    private static String join(List<?> list) {
        return list.stream().map(Objects::toString).collect(Collectors.joining(""));
    }

    private static <T> List<T> reduceList(T first, List<T> remain) {
        List<T> list = new ArrayList<>();
        list.add(first);
        list.addAll(remain);
        return list;
    }

    public static Object parse(String input) {
        return parser.parse(input);
    }
}
```