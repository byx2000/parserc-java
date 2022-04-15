package byx.parserc.interpreter;

import byx.parserc.Pair;
import byx.parserc.Parser;
import byx.parserc.interpreter.ast.*;
import byx.parserc.interpreter.runtime.Value;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static byx.parserc.Parsers.*;

/**
 * 脚本语言解释器
 */
public class Interpreter {
    // 词法元素
    private static final Parser<Character> w = chs(' ', '\t', '\r', '\n');
    private static final Parser<List<Character>> ws = w.many();
    private static final Parser<Character> alpha = range('a', 'z').or(range('A', 'Z'));
    private static final Parser<Character> digit = range('0', '9');
    private static final Parser<String> digits = digit.many1().map(Interpreter::join);
    private static final Parser<Character> underline = ch('_');
    private static final Parser<String> identifier = seq(
            oneOf(alpha, underline),
            oneOf(digit, alpha, underline).many(),
            (a, b) -> a + join(b)
    ).surroundBy(ws);
    private static final Parser<String> integer = digits.surroundBy(ws);
    private static final Parser<String> decimal = seq(digits, ch('.'), digits, (a, b, c) -> a + b + c).surroundBy(ws);
    private static final Parser<String> string = skip(ch('\'')).and(not('\'').many()).skip(ch('\'')).map(Interpreter::join).surroundBy(ws);
    private static final Parser<String> bool = string("true").or(string("false")).surroundBy(ws);
    private static final Parser<String> assign = string("=").surroundBy(ws);
    private static final Parser<String> semi = string(";").surroundBy(ws);
    private static final Parser<String> lp = string("(").surroundBy(ws);
    private static final Parser<String> rp = string(")").surroundBy(ws);
    private static final Parser<String> lb = string("{").surroundBy(ws);
    private static final Parser<String> rb = string("}").surroundBy(ws);
    private static final Parser<String> add = string("+").surroundBy(ws);
    private static final Parser<String> sub = string("-").surroundBy(ws);
    private static final Parser<String> mul = string("*").surroundBy(ws);
    private static final Parser<String> div = string("/").surroundBy(ws);
    private static final Parser<String> rem = string("%").surroundBy(ws);
    private static final Parser<String> gt = string(">").surroundBy(ws);
    private static final Parser<String> get = string(">=").surroundBy(ws);
    private static final Parser<String> lt = string("<").surroundBy(ws);
    private static final Parser<String> let = string("<=").surroundBy(ws);
    private static final Parser<String> equ = string("==").surroundBy(ws);
    private static final Parser<String> neq = string("!=").surroundBy(ws);
    private static final Parser<String> and = string("&&").surroundBy(ws);
    private static final Parser<String> or = string("||").surroundBy(ws);
    private static final Parser<String> not = string("!").surroundBy(ws);
    private static final Parser<String> var_ = string("var").surroundBy(ws);
    private static final Parser<String> if_ = string("if").surroundBy(ws);
    private static final Parser<String> else_ = string("else").surroundBy(ws);
    private static final Parser<String> for_ = string("for").surroundBy(ws);
    private static final Parser<String> while_ = string("while").surroundBy(ws);
    private static final Parser<String> break_ = string("break").surroundBy(ws);
    private static final Parser<String> continue_ = string("continue").surroundBy(ws);

    // 表达式
    private static final Parser<Expr> integerConst = integer.map(s -> new IntegerConst(Integer.parseInt(s)));
    private static final Parser<Expr> doubleConst = decimal.map(s -> new DoubleConst(Double.parseDouble(s)));
    private static final Parser<Expr> stringConst = string.map(StringConst::new);
    private static final Parser<Expr> boolConst = bool.map(s -> new BoolConst(Boolean.parseBoolean(s)));
    private static final Parser<Expr> var = identifier.map(Var::new);
    private static final Parser<Expr> e0 = oneOf(
            doubleConst,
            integerConst,
            stringConst,
            boolConst,
            var,
            skip(lp).and(lazy(Interpreter::getExpr)).skip(rp),
            skip(not).and(lazy(Interpreter::getExpr)).map(Not::new)
    );
    private static final Parser<Expr> e1 = separateBy(mul.or(div).or(rem), e0).map(Interpreter::buildExpr);
    private static final Parser<Expr> e2 = separateBy(add.or(sub), e1).map(Interpreter::buildExpr);
    private static final Parser<Expr> e3 = separateBy(let.or(lt).or(get).or(gt).or(equ).or(neq), e2).map(Interpreter::buildExpr);
    private static final Parser<Expr> e4 = separateBy(and, e3).map(Interpreter::buildExpr);
    private static final Parser<Expr> expr = separateBy(or, e4).map(Interpreter::buildExpr);

    // 语句
    private static final Parser<Statement> lazyStmt = lazy(Interpreter::getStmt);
    private static final Parser<Statement> varDeclareStmt = seq(
            var_, identifier, assign, expr,
            (a, b, c, d) -> new VarDeclaration(b, d)
    );
    private static final Parser<Statement> varAssignStmt = seq(
            identifier, assign, expr,
            (a, b, c) -> new VarAssign(a, c)
    );
    private static final Parser<Statement> block = seq(
            lb, lazyStmt.many1(), rb,
            (a, b, c) -> new Block(b)
    );
    private static final Parser<Statement> ifelse = seq(
            if_, lp, expr, rp, lazyStmt,
            optional(seq(else_, lazyStmt, (a, b) -> b)),
            (a, b, c, d, e, f) -> new IfElse(c, e, f)
    );
    private static final Parser<Statement> forLoop = skip(for_.and(lp)).and(lazyStmt).skip(semi).and(expr).skip(semi).and(lazyStmt).skip(rp).and(lazyStmt)
            .map(p -> new ForLoop(p.getFirst().getFirst().getFirst(), p.getFirst().getFirst().getSecond(), p.getFirst().getSecond(), p.getSecond()));
    private static final Parser<Statement> whileLoop = skip(while_.and(lp)).and(expr).skip(rp).and(lazyStmt)
            .map(p -> new WhileLoop(p.getFirst(), p.getSecond()));
    private static final Parser<Statement> breakStmt = break_.map(Break::new);
    private static final Parser<Statement> continueStmt = continue_.map(Continue::new);
    private static final Parser<Statement> stmt = oneOf(
            varDeclareStmt,
            varAssignStmt,
            block,
            ifelse,
            forLoop,
            whileLoop,
            breakStmt,
            continueStmt
    );

    private static final Parser<Program> program = stmt.many().map(Program::new);

    private static Parser<Expr> getExpr() {
        return expr;
    }

    private static Parser<Statement> getStmt() {
        return stmt;
    }

    private static String join(List<?> list) {
        return list.stream().map(Objects::toString).collect(Collectors.joining());
    }

    private static Expr buildExpr(Pair<Expr, List<Pair<String, Expr>>> r) {
        Expr expr = r.getFirst();
        for (Pair<String, Expr> p : r.getSecond()) {
            switch (p.getFirst()) {
                case "+":
                    expr = new Add(expr, p.getSecond());
                    break;
                case "-":
                    expr = new Sub(expr, p.getSecond());
                    break;
                case "*":
                    expr = new Mul(expr, p.getSecond());
                    break;
                case "/":
                    expr = new Div(expr, p.getSecond());
                    break;
                case "%":
                    expr = new Rem(expr, p.getSecond());
                    break;
                case ">":
                    expr = new GreaterThan(expr, p.getSecond());
                    break;
                case ">=":
                    expr = new GreaterEqualThan(expr, p.getSecond());
                    break;
                case "<":
                    expr = new LessThan(expr, p.getSecond());
                    break;
                case "<=":
                    expr = new LessEqualThan(expr, p.getSecond());
                    break;
                case "==":
                    expr = new Equal(expr, p.getSecond());
                    break;
                case "!=":
                    expr = new NotEqual(expr, p.getSecond());
                    break;
                case "&&":
                    expr = new And(expr, p.getSecond());
                    break;
                case "||":
                    expr = new Or(expr, p.getSecond());
                    break;
            }
        }
        return expr;
    }

    public static Map<String, Value> run(String s) {
        return program.parse(s).run();
    }
}
