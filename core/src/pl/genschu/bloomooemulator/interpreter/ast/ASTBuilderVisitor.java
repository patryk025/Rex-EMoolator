package pl.genschu.bloomooemulator.interpreter.ast;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.antlr.AidemMediaLexer;
import pl.genschu.bloomooemulator.interpreter.antlr.AidemMediaParser;
import pl.genschu.bloomooemulator.interpreter.antlr.AidemMediaParserBaseVisitor;
import pl.genschu.bloomooemulator.interpreter.ast.expressions.*;
import pl.genschu.bloomooemulator.interpreter.ast.statements.*;

import java.util.ArrayList;
import java.util.List;

public class ASTBuilderVisitor extends AidemMediaParserBaseVisitor<Node> {
    private final Context context;

    public ASTBuilderVisitor(Context context) {
        this.context = context;
    }

    private Expression asExpression(Node node) {
        if (node == null) {
            return null;
        }
        if (!(node instanceof Expression)) {
            throw new IllegalStateException("Expected Expression, got: " + node.getClass().getSimpleName());
        }
        return (Expression) node;
    }

    private List<Expression> buildArgList(AidemMediaParser.ArgListOptContext ctx) {
        List<Expression> args = new ArrayList<>();
        if (ctx == null) {
            return args;
        }
        for (AidemMediaParser.ArgContext a : ctx.arg()) {
            Expression e = asExpression(visit(a));
            if (e != null) {
                args.add(e);
            }
        }
        return args;
    }

    @Override
    public Node visitScript(AidemMediaParser.ScriptContext ctx) {
        List<Node> nodes = new ArrayList<>();

        for (AidemMediaParser.StatementContext st : ctx.statement()) {
            Node n = visit(st);
            if (n != null) {
                nodes.add(n);
            }
        }

        return new BlockExpression(nodes);
    }

    @Override
    public Node visitStatement(AidemMediaParser.StatementContext ctx) {
        if (ctx.specialCall() != null) {
            return visit(ctx.specialCall());
        }
        if (ctx.methodCall() != null) {
            return visit(ctx.methodCall());
        }
        if (ctx.expr() != null) {
            return visit(ctx.expr());
        }
        return null;
    }

    @Override
    public Node visitSpecialCall(AidemMediaParser.SpecialCallContext ctx) {
        String name = ctx.name.getText();
        List<Expression> args = buildArgList(ctx.argListOpt());

        switch (name) {
            case "BOOL":
            case "STRING":
            case "DOUBLE":
            case "INT": {
                if (args.size() < 2) {
                    throw new RuntimeException("@" + name + " expects at least 2 arguments");
                }

                Expression varNameExpr = args.get(0);
                String varName = varNameExpr.evaluate(context).toString();
                Expression valueExpr = args.get(1);

                return new VariableDefinitionStatement(name, varName, valueExpr);
            }

            case "CONV": {
                if (args.size() < 2) {
                    throw new RuntimeException("@CONV expects 2 arguments");
                }
                Expression castedVariable = args.get(0);
                String targetVariableType = args.get(1).evaluate(context).toString();
                return new ConvStatement(castedVariable, targetVariableType);
            }

            case "RETURN": {
                if (args.isEmpty()) {
                    return new ReturnExpression(null);
                }
                return new ReturnExpression(args.get(0));
            }

            case "BREAK":
                return new BreakStatement();

            case "ONEBREAK":
                return new OneBreakStatement();

            case "GETAPPLICATIONNAME":
                return new ConstantExpression(this.context.getGame()
                        .getApplicationVariable().getName());

            case "GETCURRENTSCENE":
                return new ConstantExpression(this.context.getGame()
                        .getCurrentScene());

            case "MSGBOX":
                // TODO: implement
                return null;

            case "IF": {
                // @IF(cond, codeTrue, codeFalse)
                // @IF(left, operator, right, codeTrue, codeFalse)
                if (args.size() < 3) {
                    throw new RuntimeException("@IF expects at least 3 arguments");
                }

                Expression cond;
                if(args.size() == 3)
                    cond = args.get(0);
                else {
                    Expression left = args.get(0);
                    String operator = args.get(1).evaluate(context).toString();
                    Expression right = args.get(2);
                    cond = new ConditionExpression(left, right, operator);
                }
                Expression trueBranch = parseCodeArg(args.get(1));
                Expression falseBranch = args.size() >= 3
                        ? parseCodeArg(args.get(2))
                        : null;

                return new IfStatement(cond, trueBranch, falseBranch);
            }

            case "WHILE": {
                // @WHILE(cond, code)
                if (args.size() < 2) {
                    throw new RuntimeException("@WHILE expects 2 arguments");
                }

                Expression cond = args.get(0);
                Expression code = parseCodeArg(args.get(1));

                // DEAD-END OPT: @WHILE(FALSE, {...}) -> nic
                if (cond instanceof ConstantExpression) {
                    ConstantExpression c = (ConstantExpression) cond;
                    Object v = c.evaluate(context);
                    boolean asBool = false;
                    if (v instanceof Boolean) {
                        asBool = (Boolean) v;
                    } else if (v instanceof Number) {
                        Number n = (Number) v;
                        asBool = n.doubleValue() != 0.0;
                    } else if (v != null) {
                        asBool = Boolean.parseBoolean(v.toString());
                    }

                    if (!asBool) {
                        return null; // pętla nigdy się nie wykona
                    }
                }

                return new WhileStatement(cond, code);
            }

            case "LOOP":
            case "FOR": {
                // @LOOP(code, start, diff, step)
                // @FOR(iterator, code, start, diff, step)
                int neededArgs = (name.equals("LOOP") ? 4 : 5);
                if (args.size() < neededArgs) {
                    throw new RuntimeException("@"+name+" expects at least "+neededArgs+" arguments");
                }

                Expression start;
                Expression end;
                Expression step;
                Expression code;
                Expression iterator;

                if (name.equals("LOOP")) {
                    code = parseCodeArg(args.get(0));
                    start = args.get(1);
                    end = args.get(2);
                    step = args.get(3);

                    return new LoopStatement(start, end, step, code);
                } else {
                    iterator = args.get(0);
                    code = parseCodeArg(args.get(1));
                    start = args.get(2);
                    end = args.get(3);
                    step = args.get(4);

                    throw new RuntimeException("@FOR not implemented yet");
                }

            }
            default: {
                throw new RuntimeException("Unknown special call: " + name);
            }
        }
    }

    @Override
    public Node visitMethodCall(AidemMediaParser.MethodCallContext ctx) {
        Expression target;
        if (ctx.objectName() != null) {
            target = asExpression(visit(ctx.objectName()));
        } else if (ctx.objectReference() != null) {
            target = asExpression(visit(ctx.objectReference()));
        } else {
            throw new RuntimeException("MethodCall without target: " + ctx.getText());
        }

        String methodName = ctx.method.getText();
        List<Expression> args = buildArgList(ctx.argListOpt());

        return new MethodCallExpression(target, methodName, args.toArray(new Expression[0]));
    }

    @Override
    public Node visitObjectName(AidemMediaParser.ObjectNameContext ctx) {
        String name = ctx.name.getText();
        if (ctx.field != null) {
            // struct: TYPE|FIELD
            String field = ctx.field.getText();
            return new StructExpression(name, field);
        } else {
            return new VariableExpression(name);
        }
    }

    @Override
    public Node visitObjectReference(AidemMediaParser.ObjectReferenceContext ctx) {
        Expression inner = asExpression(visit(ctx.primary()));
        return new PointerExpression(inner);
    }

    @Override
    public Node visitArg(AidemMediaParser.ArgContext ctx) {
        if (ctx.expr() != null) {
            return visit(ctx.expr());
        }
        if (ctx.missing_quote != null) {
            // missing quote, add missing quote to the end of the string
            return new ConstantExpression(ctx.missing_quote.getText()+"\"");
        }
        return null;
    }

    @Override
    public Node visitExpr(AidemMediaParser.ExprContext ctx) {
        // expr: addExpr
        return visit(ctx.addExpr());
    }

    @Override
    public Node visitAddExpr(AidemMediaParser.AddExprContext ctx) {
        // addExpr: left=mulExpr (op=(PLUS|MINUS) right=mulExpr)* ;
        Expression result = asExpression(visit(ctx.left));

        int childCount = ctx.getChildCount();
        for (int i = 1; i + 1 < childCount; i += 2) {
            String op = ctx.getChild(i).getText();
            Expression right = asExpression(visit(ctx.getChild(i + 1)));
            result = new ArithmeticExpression(result, right, op);
        }

        return result;
    }

    @Override
    public Node visitMulExpr(AidemMediaParser.MulExprContext ctx) {
        // mulExpr: left=unaryExpr (op=(STAR|AT|PERC) right=unaryExpr)* ;
        Expression result = asExpression(visit(ctx.left));

        int childCount = ctx.getChildCount();
        for (int i = 1; i + 1 < childCount; i += 2) {
            String op = ctx.getChild(i).getText();
            Expression right = asExpression(visit(ctx.getChild(i + 1)));
            result = new ArithmeticExpression(result, right, op);
        }

        return result;
    }

    @Override
    public Node visitUnaryExpr(AidemMediaParser.UnaryExprContext ctx) {
        if (ctx.primary() != null) {
            return visit(ctx.primary());
        }

        // unaryExpr: op=(PLUS|MINUS|STAR) unaryExpr
        String op = ctx.op.getText();
        Expression inner = asExpression(visit(ctx.unaryExpr()));

        switch (op) {
            case "+":
                return inner;
            case "-":
                // -x => 0 - x
                return new ArithmeticExpression(
                        new ConstantExpression(0),
                        inner,
                        "-"
                );
            case "*":
                // dereference
                return new PointerExpression(inner);
            default:
                throw new RuntimeException("Unknown unary operator: " + op);
        }
    }

    @Override
    public Node visitPrimary(AidemMediaParser.PrimaryContext ctx) {
        // NUMBER
        TerminalNode num = ctx.NUMBER();
        if (num != null) {
            String text = num.getText();
            if (text.contains(".")) {
                return new ConstantExpression(Double.parseDouble(text));
            } else {
                return new ConstantExpression(Integer.parseInt(text));
            }
        }

        // TRUE / FALSE
        if (ctx.TRUE() != null) {
            return new ConstantExpression("TRUE");
        }
        if (ctx.FALSE() != null) {
            return new ConstantExpression("FALSE");
        }

        // STRING
        if (ctx.STRING() != null) {
            String text = ctx.STRING().getText();
            if (text.length() >= 2 && text.startsWith("\"") && text.endsWith("\"")) {
                text = text.substring(1, text.length() - 1);
            }
            return new ConstantExpression(text);
        }

        // CODE_BLOCK: '"{' ... '}"'
        if (ctx.CODE_BLOCK() != null) {
            String raw = ctx.CODE_BLOCK().getText(); // "\"{...}\""
            String text = raw;

            // remove quotes
            if (text.length() >= 2 && text.startsWith("\"") && text.endsWith("\"")) {
                text = text.substring(1, text.length() - 1);
            }

            // parsing
            return new ConstantExpression(text);
        }

        // methodCall
        if (ctx.methodCall() != null) {
            return visit(ctx.methodCall());
        }

        // objectName
        if (ctx.objectName() != null) {
            return visit(ctx.objectName());
        }

        // [ expr ]
        if (ctx.LBRACK() != null && ctx.expr() != null) {
            return asExpression(visit(ctx.expr()));
        }

        throw new RuntimeException("Unsupported primary: " + ctx.getText());
    }

    private Expression parseCodeArg(Expression arg) {
        if (arg instanceof ConstantExpression) {
            ConstantExpression constant = (ConstantExpression) arg;
            Object value = constant.evaluate(context);
            if (value instanceof String) {
                String s = (String) value;
                return parseCodeBlockToExpression(s);
            }
        }

        return arg;
    }

    private Expression parseCodeBlockToExpression(String code) {
        String text = code.trim();

        if (!text.startsWith("{")) {
            text = "{" + text;
        }
        if (!text.endsWith("}")) {
            text = text + "}";
        }

        CharStream input = CharStreams.fromString(text);
        AidemMediaLexer lexer = new AidemMediaLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        AidemMediaParser parser = new AidemMediaParser(tokens);

        AidemMediaParser.ScriptContext scriptCtx = parser.script();

        ASTBuilderVisitor nested = new ASTBuilderVisitor(this.context);
        Node node = nested.visit(scriptCtx);

        return asExpression(node);
    }
}
