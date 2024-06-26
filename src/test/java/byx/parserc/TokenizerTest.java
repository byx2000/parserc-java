package byx.parserc;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static byx.parserc.Parsers.*;
import static org.junit.jupiter.api.Assertions.*;
import static byx.parserc.Tokenizer.*;

public class TokenizerTest {
    @Test
    public void test() {
        String s = """
            var a = 123;
            var b = 3.14;
            var c = 'hello';
            var d = [1, 2, 3.14, 'hello'];

            function fib(n) {
                if (n == 1 || n == 2) {
                    return 1;
                }
                return fib(n - 1) + fib(n - 2);
            }

            function map(f, m) {
                return m(f());
            }

            function main() {
                map(() => fib(10), a => a + 1);
            }""";
        assertEquals(List.of(
                new Token(TokenType.Identifier, "var"),
                new Token(TokenType.Identifier, "a"),
                new Token(TokenType.Assign, "="),
                new Token(TokenType.Integer, "123"),
                new Token(TokenType.Semicolon, ";"),
                new Token(TokenType.Identifier, "var"),
                new Token(TokenType.Identifier, "b"),
                new Token(TokenType.Assign, "="),
                new Token(TokenType.Decimal, "3.14"),
                new Token(TokenType.Semicolon, ";"),
                new Token(TokenType.Identifier, "var"),
                new Token(TokenType.Identifier, "c"),
                new Token(TokenType.Assign, "="),
                new Token(TokenType.String, "hello"),
                new Token(TokenType.Semicolon, ";"),
                new Token(TokenType.Identifier, "var"),
                new Token(TokenType.Identifier, "d"),
                new Token(TokenType.Assign, "="),
                new Token(TokenType.OpenSquareBracket, "["),
                new Token(TokenType.Integer, "1"),
                new Token(TokenType.Comma, ","),
                new Token(TokenType.Integer, "2"),
                new Token(TokenType.Comma, ","),
                new Token(TokenType.Decimal, "3.14"),
                new Token(TokenType.Comma, ","),
                new Token(TokenType.String, "hello"),
                new Token(TokenType.CloseSquareBracket, "]"),
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
                new Token(TokenType.Equal, "=="),
                new Token(TokenType.Integer, "1"),
                new Token(TokenType.Or, "||"),
                new Token(TokenType.Identifier, "n"),
                new Token(TokenType.Equal, "=="),
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
                new Token(TokenType.Sub, "-"),
                new Token(TokenType.Integer, "1"),
                new Token(TokenType.CloseParentheses, ")"),
                new Token(TokenType.Add, "+"),
                new Token(TokenType.Identifier, "fib"),
                new Token(TokenType.OpenParentheses, "("),
                new Token(TokenType.Identifier, "n"),
                new Token(TokenType.Sub, "-"),
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

    private static final Parser<Character> alpha = range('a', 'z').or(range('A', 'Z'));
    private static final Parser<Character> digit = range('0', '9');
    private static final Parser<Character> underline = ch('_');
    private static final Parser<Token> identifier = oneOf(alpha, underline).and(oneOf(digit, alpha, underline).many())
        .map(p -> new Token(TokenType.Identifier, p.first() + join(p.second())));
    private static final Parser<String> digits = digit.many1().map(Tokenizer::join);
    private static final Parser<Token> integer = digits.map(v -> new Token(TokenType.Integer, v));
    private static final Parser<Token> decimal = seq(digits, ch('.'), digits).map(Tokenizer::join)
        .map(s -> new Token(TokenType.Decimal, s));
    private static final Parser<Token> string = skip(ch('\'')).and(not('\'').many()).skip(ch('\''))
        .map(r -> new Token(TokenType.String, join(r)));
    private static final Parser<Token> add = createTokenParser(TokenType.Add, "+");
    private static final Parser<Token> sub = createTokenParser(TokenType.Sub, "-");
    private static final Parser<Token> mul = createTokenParser(TokenType.Mul, "*");
    private static final Parser<Token> div = createTokenParser(TokenType.Div, "/");
    private static final Parser<Token> and = createTokenParser(TokenType.And, "&&");
    private static final Parser<Token> or = createTokenParser(TokenType.Or, "||");
    private static final Parser<Token> assign = createTokenParser(TokenType.Assign, "=");
    private static final Parser<Token> equal = createTokenParser(TokenType.Equal, "==");
    private static final Parser<Token> arrow = createTokenParser(TokenType.Arrow, "=>");
    private static final Parser<Token> if_ = createTokenParser(TokenType.If, "if");
    private static final Parser<Token> for_ = createTokenParser(TokenType.For, "for");
    private static final Parser<Token> var = createTokenParser(TokenType.Var, "var");
    private static final Parser<Token> function = createTokenParser(TokenType.Function, "function");
    private static final Parser<Token> openParentheses = createTokenParser(TokenType.OpenParentheses, "(");
    private static final Parser<Token> closeParentheses = createTokenParser(TokenType.CloseParentheses, ")");
    private static final Parser<Token> openSquareBracket = createTokenParser(TokenType.OpenSquareBracket, "[");
    private static final Parser<Token> closeSquareBracket = createTokenParser(TokenType.CloseSquareBracket, "]");
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
    ).trim().many();

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

record Token(TokenType type, String value) {}
