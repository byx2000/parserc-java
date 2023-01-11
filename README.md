# parserc-java

parserc-java是用java实现的解析器组合子（Parser Combinator）库，可以方便地以自底向上的方式构建复杂的解析器。

## 计算器示例

```java
public class ExprCalc {
    private static final Parser<?> w = chs(' ', '\t', '\r', '\n');
    private static final Parser<?> ws = w.many();
    private static final Parser<?> digit = range('0', '9');
    private static final Parser<Character> add = ch('+').surround(ws);
    private static final Parser<Character> sub = ch('-').surround(ws);
    private static final Parser<Character> mul = ch('*').surround(ws);
    private static final Parser<Character> div = ch('/').surround(ws);
    private static final Parser<Character> lp = ch('(').surround(ws);
    private static final Parser<Character> rp = ch(')').surround(ws);
    private static final Parser<String> digits = digit.many1().map(ExprCalc::join);
    private static final Parser<Double> integer = digits.map(Double::parseDouble);
    private static final Parser<Double> decimal = seq(digits, ch('.'), digits).map(ExprCalc::join).map(Double::parseDouble);
    private static final Parser<Double> number = decimal.or(integer).surround(ws);
    private static final Parser<Double> bracketExpr = skip(lp).and(lazy(ExprCalc::getExpr)).skip(rp);
    private static final Parser<Double> negExpr = skip(sub).and(lazy(ExprCalc::getFact)).map(e -> -e);
    private static final Parser<Double> fact = oneOf(number, bracketExpr, negExpr);
    private static final Parser<Double> term = fact.and(mul.or(div).and(fact).many()).map(ExprCalc::calc);
    private static final Parser<Double> expr = term.and(add.or(sub).and(term).many()).map(ExprCalc::calc);

    private static Parser<Double> getFact() {
        return fact;
    }

    private static Parser<Double> getExpr() {
        return expr;
    }

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

    public static Double eval(String cursor) {
        return expr.parse(cursor);
    }
}

public class Main {
    public static void main(String[] args) {
        System.out.println(ExprCalc.eval("77.58 * (6 / 3.14 + 55.2234) - 2 * 6.1 / (1.0 + 2 / (4.0 - 3.8 * 5))"));
    }
}
```

## JSON示例

```java
public class JsonParser {
    private static final Parser<Character> w = chs(' ', '\t', '\n', '\r');
    private static final Parser<List<Character>> ws = w.many();
    private static final Parser<String> digit = range('0', '9').map(Objects::toString);
    private static final Parser<String> digits = digit.many1().map(JsonParser::join);
    private static final Parser<Integer> integer = digits.map(Integer::parseInt).surround(ws);
    private static final Parser<Double> decimal = seq(digits, ch('.'), digits).map(JsonParser::join).map(Double::parseDouble);
    private static final Parser<String> string = skip(ch('"')).and(not('"').many()).skip(ch('"')).map(JsonParser::join);
    private static final Parser<Boolean> bool = strs("true", "false").map(Boolean::parseBoolean).surround(ws);
    private static final Parser<Character> objStart = ch('{').surround(ws);
    private static final Parser<Character> objEnd = ch('}').surround(ws);
    private static final Parser<Character> arrStart = ch('[').surround(ws);
    private static final Parser<Character> arrEnd = ch(']').surround(ws);
    private static final Parser<Character> colon = ch(':').surround(ws);
    private static final Parser<Character> comma = ch(',').surround(ws);
    private static final Parser<Object> lazyJsonObj = lazy(JsonParser::getJsonObj);
    private static final Parser<List<Object>> arr = skip(arrStart).and(separate(comma, lazyJsonObj).opt(Collections.emptyList())).skip(arrEnd);
    private static final Parser<Pair<String, Object>> pair = string.skip(colon).and(lazyJsonObj);
    private static final Parser<Map<String, Object>> obj = skip(objStart).and(separate(comma, pair).opt(Collections.emptyList())).skip(objEnd)
            .map(ps -> ps.stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
    private static final Parser<Object> jsonObj = oneOf(
            decimal.mapTo(Object.class),
            integer.mapTo(Object.class),
            string.mapTo(Object.class),
            bool.mapTo(Object.class),
            arr.mapTo(Object.class),
            obj.mapTo(Object.class)
    );
    
    private static Parser<Object> getJsonObj() {
        return jsonObj;
    }

    private static String join(List<?> list) {
        return list.stream().map(Objects::toString).collect(Collectors.joining(""));
    }

    public static Object parse(String cursor) throws ParseException {
        return jsonObj.parse(cursor);
    }
}
```