package byx.parserc;

import java.util.*;
import java.util.stream.Collectors;

import static byx.parserc.Parsers.*;

/**
 * 脚本语言解释器
 */
public class Interpreter {
    public static class InterpretException extends RuntimeException {
        public InterpretException(String msg) {
            super(msg);
        }
    }

    private static class Environment {
        private final LinkedList<Map<String, Integer>> scopes = new LinkedList<>(List.of(new HashMap<>()));

        public void declareVar(String varName, Integer value) {
            Map<String, Integer> scope = scopes.get(scopes.size() - 1);
            if (scope.containsKey(varName)) {
                throw new InterpretException("变量重复定义：" + varName);
            }
            scope.put(varName, value);
        }

        public void setVar(String varName, Integer value) {
            for (int i = scopes.size() - 1; i >= 0; --i) {
                Map<String, Integer> scope = scopes.get(i);
                if (scope.containsKey(varName)) {
                    scope.put(varName, value);
                    return;
                }
            }
            throw new InterpretException("变量未定义：" + varName);
        }

        public Integer getVar(String varName) {
            for (int i = scopes.size() - 1; i >= 0; --i) {
                Map<String, Integer> scope = scopes.get(i);
                if (scope.containsKey(varName)) {
                    return scope.get(varName);
                }
            }
            throw new InterpretException("变量未定义：" + varName);
        }

        public Map<String, Integer> getVars() {
            return Collections.unmodifiableMap(scopes.get(scopes.size() - 1));
        }

        public void pushScope() {
            scopes.addLast(new HashMap<>());
        }

        public void popScope() {
            scopes.removeLast();
        }
    }

    private interface ArithmeticExpression {
        int eval(Environment env);
    }

    private static class Constant implements ArithmeticExpression {
        private final int value;

        private Constant(int value) {
            this.value = value;
        }

        @Override
        public int eval(Environment env) {
            return value;
        }
    }

    private static class Var implements ArithmeticExpression {
        private final String varName;

        private Var(String varName) {
            this.varName = varName;
        }

        @Override
        public int eval(Environment env) {
            return env.getVar(varName);
        }
    }

    private static abstract class BinaryArithmeticOp implements ArithmeticExpression {
        protected final ArithmeticExpression lhs, rhs;

        protected BinaryArithmeticOp(ArithmeticExpression lhs, ArithmeticExpression rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }
    }

    private static class AddOp extends BinaryArithmeticOp {
        public AddOp(ArithmeticExpression lhs, ArithmeticExpression rhs) {
            super(lhs, rhs);
        }

        @Override
        public int eval(Environment env) {
            return lhs.eval(env) + rhs.eval(env);
        }
    }

    private static class SubOp extends BinaryArithmeticOp {
        public SubOp(ArithmeticExpression lhs, ArithmeticExpression rhs) {
            super(lhs, rhs);
        }

        @Override
        public int eval(Environment env) {
            return lhs.eval(env) - rhs.eval(env);
        }
    }

    private static class MulOp extends BinaryArithmeticOp {
        public MulOp(ArithmeticExpression lhs, ArithmeticExpression rhs) {
            super(lhs, rhs);
        }

        @Override
        public int eval(Environment env) {
            return lhs.eval(env) * rhs.eval(env);
        }
    }

    private static class DivOp extends BinaryArithmeticOp {
        public DivOp(ArithmeticExpression lhs, ArithmeticExpression rhs) {
            super(lhs, rhs);
        }

        @Override
        public int eval(Environment env) {
            return lhs.eval(env) / rhs.eval(env);
        }
    }

    private static class RemOp extends BinaryArithmeticOp {
        public RemOp(ArithmeticExpression lhs, ArithmeticExpression rhs) {
            super(lhs, rhs);
        }

        @Override
        public int eval(Environment env) {
            return lhs.eval(env) % rhs.eval(env);
        }
    }

    private interface ConditionExpression {
        boolean eval(Environment env);
    }

    private static abstract class BinaryCompareOp implements ConditionExpression {
        protected final ArithmeticExpression lhs, rhs;

        protected BinaryCompareOp(ArithmeticExpression lhs, ArithmeticExpression rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }
    }

    private static class GreaterThanOp extends BinaryCompareOp {
        public GreaterThanOp(ArithmeticExpression lhs, ArithmeticExpression rhs) {
            super(lhs, rhs);
        }

        @Override
        public boolean eval(Environment env) {
            return lhs.eval(env) > rhs.eval(env);
        }
    }

    private static class LessThanOp extends BinaryCompareOp {
        public LessThanOp(ArithmeticExpression lhs, ArithmeticExpression rhs) {
            super(lhs, rhs);
        }

        @Override
        public boolean eval(Environment env) {
            return lhs.eval(env) < rhs.eval(env);
        }
    }

    private static class LessEqualThanOp extends BinaryCompareOp {
        public LessEqualThanOp(ArithmeticExpression lhs, ArithmeticExpression rhs) {
            super(lhs, rhs);
        }

        @Override
        public boolean eval(Environment env) {
            return lhs.eval(env) <= rhs.eval(env);
        }
    }

    private static class EqualOp extends BinaryCompareOp {
        public EqualOp(ArithmeticExpression lhs, ArithmeticExpression rhs) {
            super(lhs, rhs);
        }

        @Override
        public boolean eval(Environment env) {
            return lhs.eval(env) == rhs.eval(env);
        }
    }

    private static abstract class BinaryLoginOp implements ConditionExpression {
        protected final ConditionExpression lhs, rhs;

        protected BinaryLoginOp(ConditionExpression lhs, ConditionExpression rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }
    }

    private static class AndOp extends BinaryLoginOp {
        public AndOp(ConditionExpression lhs, ConditionExpression rhs) {
            super(lhs, rhs);
        }

        @Override
        public boolean eval(Environment env) {
            return lhs.eval(env) && rhs.eval(env);
        }
    }

    private static class OrOp extends BinaryLoginOp {
        public OrOp(ConditionExpression lhs, ConditionExpression rhs) {
            super(lhs, rhs);
        }

        @Override
        public boolean eval(Environment env) {
            return lhs.eval(env) || rhs.eval(env);
        }
    }

    private interface Statement {
        void execute(Environment env);
    }

    private static class VarDeclaration implements Statement {
        private final String varName;
        private final ArithmeticExpression expr;

        private VarDeclaration(String varName, ArithmeticExpression expr) {
            this.varName = varName;
            this.expr = expr;
        }

        @Override
        public void execute(Environment env) {
            env.declareVar(varName, expr.eval(env));
        }
    }

    private static class VarAssign implements Statement {
        private final String varName;
        private final ArithmeticExpression expr;

        private VarAssign(String varName, ArithmeticExpression expr) {
            this.varName = varName;
            this.expr = expr;
        }

        @Override
        public void execute(Environment env) {
            env.setVar(varName, expr.eval(env));
        }
    }

    private static class Block implements Statement {
        private final List<Statement> stmts;

        private Block(List<Statement> stmts) {
            this.stmts = stmts;
        }

        @Override
        public void execute(Environment env) {
            env.pushScope();
            stmts.forEach(s -> s.execute(env));
            env.popScope();
        }
    }

    private static class IfElse implements Statement {
        private final ConditionExpression cond;
        private final Statement trueBranch;
        private final Statement falseBranch;

        private IfElse(ConditionExpression cond, Statement trueBranch, Statement falseBranch) {
            this.cond = cond;
            this.trueBranch = trueBranch;
            this.falseBranch = falseBranch;
        }

        @Override
        public void execute(Environment env) {
            if (cond.eval(env)) {
                trueBranch.execute(env);
            } else if (falseBranch != null) {
                falseBranch.execute(env);
            }
        }
    }

    private static class ForLoop implements Statement {
        private final Statement init;
        private final ConditionExpression cond;
        private final Statement update;
        private final Statement body;

        private ForLoop(Statement init, ConditionExpression cond, Statement update, Statement body) {
            this.init = init;
            this.cond = cond;
            this.update = update;
            this.body = body;
        }

        @Override
        public void execute(Environment env) {
            env.pushScope();
            for (init.execute(env); cond.eval(env); update.execute(env)) {
                body.execute(env);
            }
            env.popScope();
        }
    }

    private static class Program {
        private final List<Statement> stmts;

        private Program(List<Statement> stmts) {
            this.stmts = stmts;
        }

        public Map<String, Integer> run() {
            Environment env = new Environment();
            for (Statement s : stmts) {
                s.execute(env);
            }
            return env.getVars();
        }
    }

    private static final Parser<Character> w = chs(' ', '\t', '\r', '\n');
    private static final Parser<List<Character>> ws = w.many();
    private static final Parser<Character> alpha = range('a', 'z').or(range('A', 'Z'));
    private static final Parser<Character> digit = range('0', '9');
    private static final Parser<Character> underline = ch('_');
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
    private static final Parser<String> lt = string("<").surroundBy(ws);
    private static final Parser<String> let = string("<=").surroundBy(ws);
    private static final Parser<String> equ = string("==").surroundBy(ws);
    private static final Parser<String> and = string("&&").surroundBy(ws);
    private static final Parser<String> or = string("||").surroundBy(ws);
    private static final Parser<String> var_ = string("var").surroundBy(ws);
    private static final Parser<String> if_ = string("if").surroundBy(ws);
    private static final Parser<String> else_ = string("else").surroundBy(ws);
    private static final Parser<String> for_ = string("for").surroundBy(ws);
    private static final Parser<String> identifier = seq(
            oneOf(alpha, underline),
            oneOf(digit, alpha, underline).many(),
            (a, b) -> a + join(b)
    ).surroundBy(ws);
    private static final Parser<String> integer =  digit.many1().map(Interpreter::join).surroundBy(ws);

    private static final Parser<ArithmeticExpression> constant = integer.map(n -> new Constant(Integer.parseInt(n)));
    private static final Parser<ArithmeticExpression> var = identifier.map(Var::new);
    private static final Parser<ArithmeticExpression> arithFact = oneOf(
            constant,
            var,
            skip(lp).and(lazy(Interpreter::getArithExpr)).skip(rp)
    );
    private static final Parser<ArithmeticExpression> arithTerm = separateBy(mul.or(div), arithFact)
            .map(Interpreter::buildArithmeticExpr);
    private static final Parser<ArithmeticExpression> arithExpr = separateBy(add.or(sub).or(rem), arithTerm)
            .map(Interpreter::buildArithmeticExpr);
    private static final Parser<ConditionExpression> compareExpr = seq(
            arithExpr, let.or(lt).or(gt).or(equ), arithExpr,
            (a, b, c) -> buildCompareExpr(b, a, c)
    );
    private static final Parser<ConditionExpression> andExpr = separateBy(and, compareExpr).ignoreDelimiter()
            .map(Interpreter::buildAndExpr);
    private static final Parser<ConditionExpression> orExpr = separateBy(or, andExpr).ignoreDelimiter()
            .map(Interpreter::buildOrExpr);

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
            if_, lp, orExpr, rp, lazyStmt,
            optional(seq(else_, lazyStmt, (a, b) -> b)),
            (a, b, c, d, e, f) -> new IfElse(c, e, f)
    );
    private static final Parser<Statement> forLoop = skip(for_.and(lp)).and(lazyStmt).skip(semi).and(orExpr).skip(semi).and(lazyStmt).skip(rp).and(lazyStmt)
            .map(p -> new ForLoop(p.getFirst().getFirst().getFirst(), p.getFirst().getFirst().getSecond(), p.getFirst().getSecond(), p.getSecond()));
    private static final Parser<Statement> stmt = oneOf(
            varDeclareStmt,
            varAssignStmt,
            block,
            ifelse,
            forLoop
    );

    private static final Parser<Program> program = stmt.many().map(Program::new);

    private static Parser<ArithmeticExpression> getArithExpr() {
        return arithExpr;
    }

    private static Parser<Statement> getStmt() {
        return stmt;
    }

    private static String join(List<?> list) {
        return list.stream().map(Objects::toString).collect(Collectors.joining());
    }

    private static ArithmeticExpression buildArithmeticExpr(Pair<ArithmeticExpression, List<Pair<String, ArithmeticExpression>>> p) {
        ArithmeticExpression e = p.getFirst();
        for (Pair<String, ArithmeticExpression> pp : p.getSecond()) {
            switch (pp.getFirst()) {
                case "+":
                    e = new AddOp(e, pp.getSecond());
                    break;
                case "-":
                    e = new SubOp(e, pp.getSecond());
                    break;
                case "*":
                    e = new MulOp(e, pp.getSecond());
                    break;
                case "/":
                    e = new DivOp(e, pp.getSecond());
                    break;
                case "%":
                    e = new RemOp(e, pp.getSecond());
                    break;
            }
        }
        return e;
    }

    private static ConditionExpression buildCompareExpr(String op, ArithmeticExpression lhs, ArithmeticExpression rhs) {
        switch (op) {
            case ">":
                return new GreaterThanOp(lhs, rhs);
            case "<":
                return new LessThanOp(lhs, rhs);
            case "<=":
                return new LessEqualThanOp(lhs, rhs);
            case "==":
                return new EqualOp(lhs, rhs);
        }
        throw new RuntimeException("未知的比较运算符：" + op);
    }

    private static ConditionExpression buildAndExpr(List<ConditionExpression> exprs) {
        return exprs.stream().skip(1).reduce(exprs.get(0), AndOp::new);
    }

    private static ConditionExpression buildOrExpr(List<ConditionExpression> exprs) {
        return exprs.stream().skip(1).reduce(exprs.get(0), OrOp::new);
    }

    public static Map<String, Integer> interpret(String s) {
        return Interpreter.program.parse(s).run();
    }
}
