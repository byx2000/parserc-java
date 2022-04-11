package byx.parserc;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static byx.parserc.Parsers.*;

/**
 * 词法分析器
 */
public class Tokenizer {
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

    public static class Token {
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

    private static final Parser<Character> w = chs(' ', '\t', '\r', '\n');
    private static final Parser<List<Character>> ws = w.many();
    private static final Parser<Character> alpha = range('a', 'z').or(range('A', 'Z'));
    private static final Parser<Character> digit = range('0', '9');
    private static final Parser<Character> underline = ch('_');
    private static final Parser<Token> identifier = seq(
            oneOf(alpha, underline),
            oneOf(digit, alpha, underline).many(),
            (a, b) -> new Token(TokenType.Identifier, a + join(b))
    );
    private static final Parser<String> digits = digit.many1().map(Tokenizer::join);
    private static final Parser<Token> integer = digits.map(v -> new Token(TokenType.Integer, v));
    private static final Parser<Token> decimal = seq(
            digits, ch('.'), digits,
            (a, b, c) -> new Token(TokenType.Decimal, a + b + c)
    );
    private static final Parser<Token> string = seq(
            ch('\''),
            not('\'').many(),
            ch('\''),
            (a, b, c) -> new Token(TokenType.String, join(b))
    );
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
    ).surroundBy(ws).many();

    private static String join(List<?> list) {
        return list.stream().map(Objects::toString).collect(Collectors.joining());
    }

    private static Parser<Token> createTokenParser(TokenType type, String value) {
        return string(value).map(v -> new Token(type, v));
    }

    public static List<Token> tokenize(String s) {
        return tokens.parse(s);
    }
}
