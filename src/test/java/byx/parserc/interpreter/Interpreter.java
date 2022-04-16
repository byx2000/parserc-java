package byx.parserc.interpreter;

import byx.parserc.Pair;
import byx.parserc.Parser;
import byx.parserc.interpreter.ast.*;
import byx.parserc.interpreter.runtime.Value;

import java.util.Collections;
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
    private static final Parser<String> w = chs(' ', '\t', '\r', '\n').map(Objects::toString);
    private static final Parser<String> comment = string("//").skip(not('\n').many()).skip(ch('\n'));
    private static final Parser<?> ws = w.or(comment).many();
    private static final Parser<Character> alpha = range('a', 'z').or(range('A', 'Z'));
    private static final Parser<Character> digit = range('0', '9');
    private static final Parser<String> digits = digit.many1().map(Interpreter::join);
    private static final Parser<Character> underline = ch('_');
    private static final Parser<String> identifier = oneOf(alpha, underline).and(oneOf(digit, alpha, underline).many())
            .map(p -> p.getFirst() + join(p.getSecond()))
            .surroundBy(ws);
    private static final Parser<String> integer = digits.surroundBy(ws);
    private static final Parser<String> decimal = seq(digits, ch('.'), digits, (a, b, c) -> a + b + c).surroundBy(ws);
    private static final Parser<String> string = skip(ch('\'')).and(not('\'').many()).skip(ch('\'')).map(Interpreter::join).surroundBy(ws);
    private static final Parser<String> bool = string("true").or(string("false")).surroundBy(ws);
    private static final Parser<String> assign = string("=").surroundBy(ws);
    private static final Parser<String> semi = string(";").surroundBy(ws);
    private static final Parser<String> comma = string(",").surroundBy(ws);
    private static final Parser<String> colon = string(":").surroundBy(ws);
    private static final Parser<String> dot = string(".").surroundBy(ws);
    private static final Parser<String> lp = string("(").surroundBy(ws);
    private static final Parser<String> rp = string(")").surroundBy(ws);
    private static final Parser<String> lb = string("{").surroundBy(ws);
    private static final Parser<String> rb = string("}").surroundBy(ws);
    private static final Parser<String> ls = string("[").surroundBy(ws);
    private static final Parser<String> rs = string("]").surroundBy(ws);
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
    private static final Parser<String> arrow = string("=>").surroundBy(ws);
    private static final Parser<String> var_ = string("var").surroundBy(ws);
    private static final Parser<String> if_ = string("if").surroundBy(ws);
    private static final Parser<String> else_ = string("else").surroundBy(ws);
    private static final Parser<String> for_ = string("for").surroundBy(ws);
    private static final Parser<String> while_ = string("while").surroundBy(ws);
    private static final Parser<String> break_ = string("break").surroundBy(ws);
    private static final Parser<String> continue_ = string("continue").surroundBy(ws);
    private static final Parser<String> return_ = string("return").surroundBy(ws);

    private static final Parser<Statement> lazyStmt = lazy(Interpreter::getStmt);
    private static final Parser<List<Statement>> stmts = lazyStmt.skip(semi.optional()).many1();
    private static final Parser<Expr> lazyExpr = lazy(Interpreter::getExpr);

    // 表达式
    private static final Parser<Expr> integerConst = integer.map(s -> new IntegerConst(Integer.parseInt(s)));
    private static final Parser<Expr> doubleConst = decimal.map(s -> new DoubleConst(Double.parseDouble(s)));
    private static final Parser<Expr> stringConst = string.map(StringConst::new);
    private static final Parser<Expr> boolConst = bool.map(s -> new BoolConst(Boolean.parseBoolean(s)));
    private static final Parser<Expr> var = identifier.map(Var::new);
    private static final Parser<List<String>> singleParamList = identifier.map(s -> List.of(s));
    private static final Parser<List<String>> paramList = skip(lp)
            .and(separateBy(comma, identifier).ignoreDelimiter().optional(Collections.emptyList()))
            .skip(rp)
            .or(singleParamList);
    private static final Parser<Expr> singleStmtFunc = paramList.skip(arrow).and(lazyExpr)
            .map(p -> new FunctionExpr(p.getFirst(), new Return(p.getSecond())));
    private static final Parser<Expr> multiStmtFunc = paramList.skip(arrow.and(lb)).and(stmts).skip(rb)
            .map(p -> new FunctionExpr(p.getFirst(), new Block(p.getSecond())));
    private static final Parser<Expr> func = singleStmtFunc.or(multiStmtFunc);
    private static final Parser<List<Expr>> argList = skip(lp)
            .and(separateBy(comma, lazyExpr).ignoreDelimiter().optional(Collections.emptyList()))
            .skip(rp);
    private static final Parser<Pair<String, Expr>> propPair = identifier.skip(colon).and(lazyExpr);
    private static final Parser<Expr> obj = skip(lb).and(separateBy(comma, propPair).ignoreDelimiter()).skip(rb)
            .map(ps -> new ObjectExpr(ps.stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))));
    private static final Parser<Expr> arr = skip(ls).and(separateBy(comma, lazyExpr).ignoreDelimiter().optional(Collections.emptyList())).skip(rs)
            .map(Array::new);
    private static final Parser<Expr> subscript = skip(ls).and(lazyExpr).skip(rs);
    @SuppressWarnings("unchecked")
    private static final Parser<Expr> e0 = oneOf(
            doubleConst,
            integerConst,
            stringConst,
            boolConst,
            func,
            var,
            obj,
            arr,
            skip(lp).and(lazyExpr).skip(rp),
            skip(not).and(lazyExpr).map(Not::new)
    ).and(argList.mapTo(Object.class)
            .or(skip(dot).and(identifier).mapTo(Object.class))
            .or(subscript.mapTo(Object.class))
            .many()).map(p -> {
        Expr e = p.getFirst();
        for (Object o : p.getSecond()) {
            if (o instanceof List) {
                e = new Call(e, (List<Expr>) o);
            } else if (o instanceof String) {
                e = new Prop(e, (String) o);
            } else if (o instanceof Expr) {
                e = new Subscript(e, (Expr) o);
            }
        }
        return e;
    });
    private static final Parser<Expr> e1 = separateBy(mul.or(div).or(rem), e0).map(Interpreter::buildExpr);
    private static final Parser<Expr> e2 = separateBy(add.or(sub), e1).map(Interpreter::buildExpr);
    private static final Parser<Expr> e3 = separateBy(let.or(lt).or(get).or(gt).or(equ).or(neq), e2).map(Interpreter::buildExpr);
    private static final Parser<Expr> e4 = separateBy(and, e3).map(Interpreter::buildExpr);
    private static final Parser<Expr> expr = separateBy(or, e4).map(Interpreter::buildExpr);

    // 语句
    private static final Parser<Statement> varDeclareStmt = skip(var_).and(identifier).skip(assign).and(expr)
            .map(p -> new VarDeclaration(p.getFirst(), p.getSecond()));
    private static final Parser<Statement> varAssignStmt = identifier.skip(assign).and(expr)
            .map(p -> new VarAssign(p.getFirst(), p.getSecond()));
    private static final Parser<Statement> block = skip(lb).and(stmts).skip(rb)
            .map(Block::new);
    private static final Parser<Statement> ifelse = skip(if_.and(lp)).and(expr).skip(rp)
            .and(lazyStmt)
            .and(skip(else_).and(lazyStmt).optional(new EmptyStatement()))
            .map(p -> new IfElse(p.getFirst().getFirst(), p.getFirst().getSecond(), p.getSecond()));
    private static final Parser<Statement> forLoop = skip(for_.and(lp)).and(lazyStmt).skip(semi).and(expr).skip(semi).and(lazyStmt).skip(rp).and(lazyStmt)
            .map(p -> new ForLoop(p.getFirst().getFirst().getFirst(), p.getFirst().getFirst().getSecond(), p.getFirst().getSecond(), p.getSecond()));
    private static final Parser<Statement> whileLoop = skip(while_.and(lp)).and(expr).skip(rp).and(lazyStmt)
            .map(p -> new WhileLoop(p.getFirst(), p.getSecond()));
    private static final Parser<Statement> breakStmt = break_.map(Break::new);
    private static final Parser<Statement> continueStmt = continue_.map(Continue::new);
    private static final Parser<Statement> returnStmt = skip(return_).and(expr).map(Return::new);
    private static final Parser<Statement> stmt = oneOf(
            varDeclareStmt,
            varAssignStmt,
            block,
            ifelse,
            forLoop,
            whileLoop,
            breakStmt,
            continueStmt,
            returnStmt,
            expr.map(e -> e::eval)
    );

    private static final Parser<Program> program = stmts.map(Program::new);

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
