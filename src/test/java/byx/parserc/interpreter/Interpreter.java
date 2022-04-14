package byx.parserc.interpreter;

import byx.parserc.Pair;
import byx.parserc.Parser;
import byx.parserc.interpreter.ast.*;

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
    private static final Parser<Character> underline = ch('_');
    private static final Parser<String> identifier = seq(
            oneOf(alpha, underline),
            oneOf(digit, alpha, underline).many(),
            (a, b) -> a + join(b)
    ).surroundBy(ws);
    private static final Parser<String> integer = digit.many1().map(Interpreter::join).surroundBy(ws);
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
    private static final Parser<ArithmeticExpr> constant = integer.map(n -> new Constant(Integer.parseInt(n)));
    private static final Parser<ArithmeticExpr> var = identifier.map(Var::new);
    private static final Parser<ArithmeticExpr> arithFact = oneOf(
            constant,
            var,
            skip(lp).and(lazy(Interpreter::getArithExpr)).skip(rp)
    );
    private static final Parser<ArithmeticExpr> arithTerm = separateBy(mul.or(div).or(rem), arithFact)
            .map(Interpreter::buildArithmeticExpr);
    private static final Parser<ArithmeticExpr> arithExpr = separateBy(add.or(sub), arithTerm)
            .map(Interpreter::buildArithmeticExpr);
    private static final Parser<ConditionExpr> compareExpr = seq(
            arithExpr, let.or(get).or(lt).or(gt).or(equ), arithExpr,
            (a, b, c) -> buildCompareExpr(b, a, c)
    );
    private static final Parser<ConditionExpr> condFact = oneOf(
            compareExpr,
            skip(lp).and(lazy(Interpreter::getCondExpr)).skip(rp),
            skip(not.and(lp)).and(lazy(Interpreter::getCondExpr)).skip(rp).map(Interpreter::buildNotExpr)
    );
    private static final Parser<ConditionExpr> condTerm = separateBy(and, condFact).map(Interpreter::buildConditionalExpr);
    private static final Parser<ConditionExpr> condExpr = separateBy(or, condTerm).map(Interpreter::buildConditionalExpr);

    // 语句
    private static final Parser<Statement> lazyStmt = lazy(Interpreter::getStmt);
    private static final Parser<Statement> varDeclareStmt = seq(
            var_, identifier, assign, arithExpr,
            (a, b, c, d) -> new VarDeclaration(b, d)
    );
    private static final Parser<Statement> varAssignStmt = seq(
            identifier, assign, arithExpr,
            (a, b, c) -> new VarAssign(a, c)
    );
    private static final Parser<Statement> block = seq(
            lb, lazyStmt.many1(), rb,
            (a, b, c) -> new Block(b)
    );
    private static final Parser<Statement> ifelse = seq(
            if_, lp, condExpr, rp, lazyStmt,
            optional(seq(else_, lazyStmt, (a, b) -> b)),
            (a, b, c, d, e, f) -> new IfElse(c, e, f)
    );
    private static final Parser<Statement> forLoop = skip(for_.and(lp)).and(lazyStmt).skip(semi).and(condExpr).skip(semi).and(lazyStmt).skip(rp).and(lazyStmt)
            .map(p -> new ForLoop(p.getFirst().getFirst().getFirst(), p.getFirst().getFirst().getSecond(), p.getFirst().getSecond(), p.getSecond()));
    private static final Parser<Statement> whileLoop = skip(while_.and(lp)).and(condExpr).skip(rp).and(lazyStmt)
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

    private static Parser<ArithmeticExpr> getArithExpr() {
        return arithExpr;
    }

    private static Parser<ConditionExpr> getCondExpr() {
        return condExpr;
    }

    private static Parser<Statement> getStmt() {
        return stmt;
    }

    private static String join(List<?> list) {
        return list.stream().map(Objects::toString).collect(Collectors.joining());
    }

    private static ArithmeticExpr buildArithmeticExpr(Pair<ArithmeticExpr, List<Pair<String, ArithmeticExpr>>> r) {
        ArithmeticExpr expr = r.getFirst();
        for (Pair<String, ArithmeticExpr> p : r.getSecond()) {
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
            }
        }
        return expr;
    }

    private static ConditionExpr buildCompareExpr(String op, ArithmeticExpr lhs, ArithmeticExpr rhs) {
        switch (op) {
            case ">":
                return new GreaterThan(lhs, rhs);
            case ">=":
                return new GreaterEqualThan(lhs, rhs);
            case "<":
                return new LessThan(lhs, rhs);
            case "<=":
                return new LessEqualThan(lhs, rhs);
            case "==":
                return new Equal(lhs, rhs);
        }
        throw new RuntimeException("未知的比较运算符：" + op);
    }

    private static ConditionExpr buildNotExpr(ConditionExpr expr) {
        return new Not(expr);
    }

    private static ConditionExpr buildConditionalExpr(Pair<ConditionExpr, List<Pair<String, ConditionExpr>>> r) {
        ConditionExpr expr = r.getFirst();
        for (Pair<String, ConditionExpr> p : r.getSecond()) {
            switch (p.getFirst()) {
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

    public static Map<String, Integer> run(String s) {
        return program.parse(s).run();
    }
}
