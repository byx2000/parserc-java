package byx.parserc;

import org.junit.jupiter.api.Test;

import java.util.List;

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
