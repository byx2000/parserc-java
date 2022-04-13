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

    private static class BreakException extends RuntimeException {
        public BreakException() {
            super(null, null, false, false);
        }
    }

    private static final BreakException BREAK_EXCEPTION = new BreakException();

    private static class ContinueException extends RuntimeException {
        public ContinueException() {
            super(null, null, false, false);
        }
    }

    private static final ContinueException CONTINUE_EXCEPTION = new ContinueException();

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

    private interface ArithmeticExpr {
        int eval(Environment env);
    }

    private static class Constant implements ArithmeticExpr {
        private final int value;

        private Constant(int value) {
            this.value = value;
        }

        @Override
        public int eval(Environment env) {
            return value;
        }
    }

    private static class Var implements ArithmeticExpr {
        private final String varName;

        private Var(String varName) {
            this.varName = varName;
        }

        @Override
        public int eval(Environment env) {
            return env.getVar(varName);
        }
    }

    private static abstract class BinaryArithmeticOp implements ArithmeticExpr {
        protected final ArithmeticExpr lhs, rhs;

        protected BinaryArithmeticOp(ArithmeticExpr lhs, ArithmeticExpr rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }
    }

    private static class AddOp extends BinaryArithmeticOp {
        public AddOp(ArithmeticExpr lhs, ArithmeticExpr rhs) {
            super(lhs, rhs);
        }

        @Override
        public int eval(Environment env) {
            return lhs.eval(env) + rhs.eval(env);
        }
    }

    private static class SubOp extends BinaryArithmeticOp {
        public SubOp(ArithmeticExpr lhs, ArithmeticExpr rhs) {
            super(lhs, rhs);
        }

        @Override
        public int eval(Environment env) {
            return lhs.eval(env) - rhs.eval(env);
        }
    }

    private static class MulOp extends BinaryArithmeticOp {
        public MulOp(ArithmeticExpr lhs, ArithmeticExpr rhs) {
            super(lhs, rhs);
        }

        @Override
        public int eval(Environment env) {
            return lhs.eval(env) * rhs.eval(env);
        }
    }

    private static class DivOp extends BinaryArithmeticOp {
        public DivOp(ArithmeticExpr lhs, ArithmeticExpr rhs) {
            super(lhs, rhs);
        }

        @Override
        public int eval(Environment env) {
            return lhs.eval(env) / rhs.eval(env);
        }
    }

    private static class RemOp extends BinaryArithmeticOp {
        public RemOp(ArithmeticExpr lhs, ArithmeticExpr rhs) {
            super(lhs, rhs);
        }

        @Override
        public int eval(Environment env) {
            return lhs.eval(env) % rhs.eval(env);
        }
    }

    private interface ConditionExpr {
        boolean eval(Environment env);
    }

    private static abstract class BinaryCompareOp implements ConditionExpr {
        protected final ArithmeticExpr lhs, rhs;

        protected BinaryCompareOp(ArithmeticExpr lhs, ArithmeticExpr rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }
    }

    private static class GreaterThanOp extends BinaryCompareOp {
        public GreaterThanOp(ArithmeticExpr lhs, ArithmeticExpr rhs) {
            super(lhs, rhs);
        }

        @Override
        public boolean eval(Environment env) {
            return lhs.eval(env) > rhs.eval(env);
        }
    }

    private static class GreaterEqualThanOp extends BinaryCompareOp {
        public GreaterEqualThanOp(ArithmeticExpr lhs, ArithmeticExpr rhs) {
            super(lhs, rhs);
        }

        @Override
        public boolean eval(Environment env) {
            return lhs.eval(env) >= rhs.eval(env);
        }
    }

    private static class LessThanOp extends BinaryCompareOp {
        public LessThanOp(ArithmeticExpr lhs, ArithmeticExpr rhs) {
            super(lhs, rhs);
        }

        @Override
        public boolean eval(Environment env) {
            return lhs.eval(env) < rhs.eval(env);
        }
    }

    private static class LessEqualThanOp extends BinaryCompareOp {
        public LessEqualThanOp(ArithmeticExpr lhs, ArithmeticExpr rhs) {
            super(lhs, rhs);
        }

        @Override
        public boolean eval(Environment env) {
            return lhs.eval(env) <= rhs.eval(env);
        }
    }

    private static class EqualOp extends BinaryCompareOp {
        public EqualOp(ArithmeticExpr lhs, ArithmeticExpr rhs) {
            super(lhs, rhs);
        }

        @Override
        public boolean eval(Environment env) {
            return lhs.eval(env) == rhs.eval(env);
        }
    }

    private static abstract class BinaryLoginOp implements ConditionExpr {
        protected final ConditionExpr lhs, rhs;

        protected BinaryLoginOp(ConditionExpr lhs, ConditionExpr rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }
    }

    private static class AndOp extends BinaryLoginOp {
        public AndOp(ConditionExpr lhs, ConditionExpr rhs) {
            super(lhs, rhs);
        }

        @Override
        public boolean eval(Environment env) {
            return lhs.eval(env) && rhs.eval(env);
        }
    }

    private static class OrOp extends BinaryLoginOp {
        public OrOp(ConditionExpr lhs, ConditionExpr rhs) {
            super(lhs, rhs);
        }

        @Override
        public boolean eval(Environment env) {
            return lhs.eval(env) || rhs.eval(env);
        }
    }

    private static class NotOp implements ConditionExpr {
        private final ConditionExpr e;

        private NotOp(ConditionExpr e) {
            this.e = e;
        }

        @Override
        public boolean eval(Environment env) {
            return !e.eval(env);
        }
    }

    private interface Statement {
        void execute(Environment env);
    }

    private static class VarDeclaration implements Statement {
        private final String varName;
        private final ArithmeticExpr expr;

        public VarDeclaration(String varName, ArithmeticExpr expr) {
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
        private final ArithmeticExpr expr;

        public VarAssign(String varName, ArithmeticExpr expr) {
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

        public Block(List<Statement> stmts) {
            this.stmts = stmts;
        }

        @Override
        public void execute(Environment env) {
            env.pushScope();
            try {
                stmts.forEach(s -> s.execute(env));
            } finally {
                env.popScope();
            }
        }
    }

    private static class IfElse implements Statement {
        private final ConditionExpr cond;
        private final Statement trueBranch;
        private final Statement falseBranch;

        public IfElse(ConditionExpr cond, Statement trueBranch, Statement falseBranch) {
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
        private final ConditionExpr cond;
        private final Statement update;
        private final Statement body;

        public ForLoop(Statement init, ConditionExpr cond, Statement update, Statement body) {
            this.init = init;
            this.cond = cond;
            this.update = update;
            this.body = body;
        }

        @Override
        public void execute(Environment env) {
            env.pushScope();
            for (init.execute(env); cond.eval(env); update.execute(env)) {
                try {
                    body.execute(env);
                } catch (BreakException e) {
                    break;
                } catch (ContinueException e) {}
            }
            env.popScope();
        }
    }

    private static class WhileLoop implements Statement {
        private final ConditionExpr cond;
        private final Statement body;

        public WhileLoop(ConditionExpr cond, Statement body) {
            this.cond = cond;
            this.body = body;
        }

        @Override
        public void execute(Environment env) {
            while (cond.eval(env)) {
                try {
                    body.execute(env);
                } catch (BreakException e) {
                    break;
                } catch (ContinueException e) {}
            }
        }
    }

    private static class Break implements Statement {
        @Override
        public void execute(Environment env) {
            throw BREAK_EXCEPTION;
        }
    }

    private static class Continue implements Statement {
        @Override
        public void execute(Environment env) {
            throw CONTINUE_EXCEPTION;
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
                    expr = new AddOp(expr, p.getSecond());
                    break;
                case "-":
                    expr = new SubOp(expr, p.getSecond());
                    break;
                case "*":
                    expr = new MulOp(expr, p.getSecond());
                    break;
                case "/":
                    expr = new DivOp(expr, p.getSecond());
                    break;
                case "%":
                    expr = new RemOp(expr, p.getSecond());
                    break;
            }
        }
        return expr;
    }

    private static ConditionExpr buildCompareExpr(String op, ArithmeticExpr lhs, ArithmeticExpr rhs) {
        switch (op) {
            case ">":
                return new GreaterThanOp(lhs, rhs);
            case ">=":
                return new GreaterEqualThanOp(lhs, rhs);
            case "<":
                return new LessThanOp(lhs, rhs);
            case "<=":
                return new LessEqualThanOp(lhs, rhs);
            case "==":
                return new EqualOp(lhs, rhs);
        }
        throw new RuntimeException("未知的比较运算符：" + op);
    }

    private static ConditionExpr buildNotExpr(ConditionExpr expr) {
        return new NotOp(expr);
    }

    private static ConditionExpr buildConditionalExpr(Pair<ConditionExpr, List<Pair<String, ConditionExpr>>> r) {
        ConditionExpr expr = r.getFirst();
        for (Pair<String, ConditionExpr> p : r.getSecond()) {
            switch (p.getFirst()) {
                case "&&":
                    expr = new AndOp(expr, p.getSecond());
                    break;
                case "||":
                    expr = new OrOp(expr, p.getSecond());
                    break;
            }
        }
        return expr;
    }

    public static Map<String, Integer> run(String s) {
        return program.parse(s).run();
    }
}
