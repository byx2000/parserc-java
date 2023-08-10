package byx.parserc;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static byx.parserc.Parsers.*;
import static byx.parserc.Parsers.str;
import static org.junit.jupiter.api.Assertions.*;
import static byx.parserc.Tokenizer.*;

public class TokenizerTest {
    @Test
    public void test() {
        String s = "var a = 123;\n" +
                "var b = 3.14;\n" +
                "var c = 'hello';\n" +
                "var d = [1, 2, 3.14, 'hello'];\n" +
                "\n" +
                "function fib(n) {\n" +
                "    if (n == 1 || n == 2) {\n" +
                "        return 1;\n" +
                "    }\n" +
                "    return fib(n - 1) + fib(n - 2);\n" +
                "}\n" +
                "\n" +
                "function map(f, m) {\n" +
                "    return m(f());\n" +
                "}\n" +
                "\n" +
                "function main() {\n" +
                "    map(() => fib(10), a => a + 1);\n" +
                "}";
        assertEquals(List.of(
                new Token(TokenType.Identifier, "var"),
                new Token(TokenType.Identifier, "a"),
                new Token(TokenType.Add, "="),
                new Token(TokenType.Integer, "123"),
                new Token(TokenType.Semicolon, ";"),
                new Token(TokenType.Identifier, "var"),
                new Token(TokenType.Identifier, "b"),
                new Token(TokenType.Add, "="),
                new Token(TokenType.Decimal, "3.14"),
                new Token(TokenType.Semicolon, ";"),
                new Token(TokenType.Identifier, "var"),
                new Token(TokenType.Identifier, "c"),
                new Token(TokenType.Add, "="),
                new Token(TokenType.String, "hello"),
                new Token(TokenType.Semicolon, ";"),
                new Token(TokenType.Identifier, "var"),
                new Token(TokenType.Identifier, "d"),
                new Token(TokenType.Add, "="),
                new Token(TokenType.OpenSquareBracket, "["),
                new Token(TokenType.Integer, "1"),
                new Token(TokenType.Comma, ","),
                new Token(TokenType.Integer, "2"),
                new Token(TokenType.Comma, ","),
                new Token(TokenType.Decimal, "3.14"),
                new Token(TokenType.Comma, ","),
                new Token(TokenType.String, "hello"),
                new Token(TokenType.CloseParentheses, "]"),
                new Token(TokenType.Semicolon, ";"),
                new Token(TokenType.Identifier, "function"),
                new Token(TokenType.Identifier, "fib"),
                new Token(TokenType.OpenParentheses, "("),
                new Token(TokenType.Identifier, "n"),
                new Token(TokenType.CloseParentheses, ")"),
                new Token(TokenType.OpenCurlyBraces, "{"),
                new Token(TokenType.Identifier, "if"),
                new Token(TokenType.OpenParentheses, "("),
                new Token(TokenType.Identifier, "n"),
                new Token(TokenType.Add, "=="),
                new Token(TokenType.Integer, "1"),
                new Token(TokenType.Add, "||"),
                new Token(TokenType.Identifier, "n"),
                new Token(TokenType.Add, "=="),
                new Token(TokenType.Integer, "2"),
                new Token(TokenType.CloseParentheses, ")"),
                new Token(TokenType.OpenCurlyBraces, "{"),
                new Token(TokenType.Identifier, "return"),
                new Token(TokenType.Integer, "1"),
                new Token(TokenType.Semicolon, ";"),
                new Token(TokenType.CloseCurlyBraces, "}"),
                new Token(TokenType.Identifier, "return"),
                new Token(TokenType.Identifier, "fib"),
                new Token(TokenType.OpenParentheses, "("),
                new Token(TokenType.Identifier, "n"),
                new Token(TokenType.Add, "-"),
                new Token(TokenType.Integer, "1"),
                new Token(TokenType.CloseParentheses, ")"),
                new Token(TokenType.Add, "+"),
                new Token(TokenType.Identifier, "fib"),
                new Token(TokenType.OpenParentheses, "("),
                new Token(TokenType.Identifier, "n"),
                new Token(TokenType.Add, "-"),
                new Token(TokenType.Integer, "2"),
                new Token(TokenType.CloseParentheses, ")"),
                new Token(TokenType.Semicolon, ";"),
                new Token(TokenType.CloseCurlyBraces, "}"),
                new Token(TokenType.Identifier, "function"),
                new Token(TokenType.Identifier, "map"),
                new Token(TokenType.OpenParentheses, "("),
                new Token(TokenType.Identifier, "f"),
                new Token(TokenType.Comma, ","),
                new Token(TokenType.Identifier, "m"),
                new Token(TokenType.CloseParentheses, ")"),
                new Token(TokenType.OpenCurlyBraces, "{"),
                new Token(TokenType.Identifier, "return"),
                new Token(TokenType.Identifier, "m"),
                new Token(TokenType.OpenParentheses, "("),
                new Token(TokenType.Identifier, "f"),
                new Token(TokenType.OpenParentheses, "("),
                new Token(TokenType.CloseParentheses, ")"),
                new Token(TokenType.CloseParentheses, ")"),
                new Token(TokenType.Semicolon, ";"),
                new Token(TokenType.CloseCurlyBraces, "}"),
                new Token(TokenType.Identifier, "function"),
                new Token(TokenType.Identifier, "main"),
                new Token(TokenType.OpenParentheses, "("),
                new Token(TokenType.CloseParentheses, ")"),
                new Token(TokenType.OpenCurlyBraces, "{"),
                new Token(TokenType.Identifier, "map"),
                new Token(TokenType.OpenParentheses, "("),
                new Token(TokenType.OpenParentheses, "("),
                new Token(TokenType.CloseParentheses, ")"),
                new Token(TokenType.Arrow, "=>"),
                new Token(TokenType.Identifier, "fib"),
                new Token(TokenType.OpenParentheses, "("),
                new Token(TokenType.Integer, "10"),
                new Token(TokenType.CloseParentheses, ")"),
                new Token(TokenType.Comma, ","),
                new Token(TokenType.Identifier, "a"),
                new Token(TokenType.Arrow, "=>"),
                new Token(TokenType.Identifier, "a"),
                new Token(TokenType.Add, "+"),
                new Token(TokenType.Integer, "1"),
                new Token(TokenType.CloseParentheses, ")"),
                new Token(TokenType.Semicolon, ";"),
                new Token(TokenType.CloseCurlyBraces, "}")
        ), Tokenizer.tokenize(s));
    }
}

/**
 * 词法分析器
 */
class Tokenizer {
    public enum TokenType {
        Identifier,
        Integer,
        Decimal,
        String,
        Add,
        Sub,
        Mul,
        Div,
        And,
        Or,
        Assign,
        Equal,
        Arrow,
        If,
        For,
        Var,
        Function,
        OpenParentheses,
        CloseParentheses,
        OpenSquareBracket,
        CloseSquareBracket,
        OpenCurlyBraces,
        CloseCurlyBraces,
        Semicolon,
        Comma
    }

    private static final Parser<Character> w = chs(' ', '\t', '\r', '\n');
    private static final Parser<List<Character>> ws = w.many();
    private static final Parser<Character> alpha = range('a', 'z').or(range('A', 'Z'));
    private static final Parser<Character> digit = range('0', '9');
    private static final Parser<Character> underline = ch('_');
    private static final Parser<Token> identifier = oneOf(alpha, underline).and(oneOf(digit, alpha, underline).many())
        .map(p -> new Token(TokenType.Identifier, p.getFirst() + join(p.getSecond())));
    private static final Parser<String> digits = digit.many1().map(Tokenizer::join);
    private static final Parser<Token> integer = digits.map(v -> new Token(TokenType.Integer, v));
    private static final Parser<Token> decimal = seq(digits, ch('.'), digits).map(Tokenizer::join)
        .map(s -> new Token(TokenType.Decimal, s));
    private static final Parser<Token> string = skip(ch('\'')).and(not('\'').many()).skip(ch('\''))
        .map(r -> new Token(TokenType.String, join(r)));
    private static final Parser<Token> add = createTokenParser(TokenType.Add, "+");
    private static final Parser<Token> sub = createTokenParser(TokenType.Add, "-");
    private static final Parser<Token> mul = createTokenParser(TokenType.Add, "*");
    private static final Parser<Token> div = createTokenParser(TokenType.Add, "/");
    private static final Parser<Token> and = createTokenParser(TokenType.Add, "&&");
    private static final Parser<Token> or = createTokenParser(TokenType.Add, "||");
    private static final Parser<Token> assign = createTokenParser(TokenType.Add, "=");
    private static final Parser<Token> equal = createTokenParser(TokenType.Add, "==");
    private static final Parser<Token> arrow = createTokenParser(TokenType.Arrow, "=>");
    private static final Parser<Token> if_ = createTokenParser(TokenType.Add, "if");
    private static final Parser<Token> for_ = createTokenParser(TokenType.Add, "for");
    private static final Parser<Token> var = createTokenParser(TokenType.Add, "var");
    private static final Parser<Token> function = createTokenParser(TokenType.Add, "function");
    private static final Parser<Token> openParentheses = createTokenParser(TokenType.OpenParentheses, "(");
    private static final Parser<Token> closeParentheses = createTokenParser(TokenType.CloseParentheses, ")");
    private static final Parser<Token> openSquareBracket = createTokenParser(TokenType.OpenSquareBracket, "[");
    private static final Parser<Token> closeSquareBracket = createTokenParser(TokenType.CloseParentheses, "]");
    private static final Parser<Token> openCurlyBraces = createTokenParser(TokenType.OpenCurlyBraces, "{");
    private static final Parser<Token> closeCurlyBraces = createTokenParser(TokenType.CloseCurlyBraces, "}");
    private static final Parser<Token> semi = createTokenParser(TokenType.Semicolon, ";");
    private static final Parser<Token> comma = createTokenParser(TokenType.Comma, ",");
    private static final Parser<List<Token>> tokens = oneOf(
        identifier,
        decimal,
        integer,
        string,
        add, sub, mul, div, and, or, equal, arrow, assign,
        if_, for_, var, function,
        openParentheses, closeParentheses, openSquareBracket, closeSquareBracket, openCurlyBraces, closeCurlyBraces,
        semi, comma
    ).surround(ws).many();

    private static String join(List<?> list) {
        return list.stream().map(Objects::toString).collect(Collectors.joining());
    }

    private static Parser<Token> createTokenParser(TokenType type, String value) {
        return str(value).map(v -> new Token(type, v));
    }

    public static List<Token> tokenize(String s) {
        return tokens.parse(s);
    }
}

class Token {
    private final TokenType type;
    private final String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return type == token.type && Objects.equals(value, token.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @Override
    public String toString() {
        return "Token{" +
            "type=" + type +
            ", value='" + value + '\'' +
            '}';
    }
}
