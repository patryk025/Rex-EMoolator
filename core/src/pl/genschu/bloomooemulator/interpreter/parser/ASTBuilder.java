package pl.genschu.bloomooemulator.interpreter.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import pl.genschu.bloomooemulator.interpreter.ast.*;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.antlr.AidemMediaLexer;
import pl.genschu.bloomooemulator.interpreter.antlr.AidemMediaParser;
import pl.genschu.bloomooemulator.interpreter.antlr.AidemMediaParserBaseVisitor;
import pl.genschu.bloomooemulator.interpreter.errors.ParseException;
import pl.genschu.bloomooemulator.interpreter.errors.SourceLocation;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.ArrayList;
import java.util.List;

/**
 * New AST tree builder using ANTLR v4.
 */
public class ASTBuilder extends AidemMediaParserBaseVisitor<ASTNode> {
    private final Context context;  // Can be null for standalone parsing

    /**
     * Creates ASTBuilder with context (for resolving variables and accessing Game).
     */
    public ASTBuilder(Context context) {
        this.context = context;
    }

    /**
     * Creates ASTBuilder without context (for parsing inline code).
     */
    public ASTBuilder() {
        this(null);
    }

    /**
     * Static helper method to parse code string to AST.
     *
     * @param code Code to parse
     * @param name Name for error messages
     * @return Parsed AST
     */
    public static ASTNode parseCode(String code, String name) {
        try {
            CharStream input = CharStreams.fromString(code);
            AidemMediaLexer lexer = new AidemMediaLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            AidemMediaParser parser = new AidemMediaParser(tokens);

            // Create builder without context
            ASTBuilder builder = new ASTBuilder();

            // Parse and return
            return builder.visit(parser.script());
        } catch (Exception e) {
            // Return empty block on error
            return new BlockNode(List.of(), SourceLocation.UNKNOWN);
        }
    }

    // ========================================
    // HELPER METHODS
    // ========================================

    /**
     * Extracts SourceLocation from a parser context.
     */
    private SourceLocation loc(ParserRuleContext ctx) {
        if (ctx == null) {
            return SourceLocation.UNKNOWN;
        }

        Token start = ctx.getStart();
        String snippet = ctx.getText();

        // Truncate if too long
        if (snippet.length() > 60) {
            snippet = snippet.substring(0, 57) + "...";
        }

        return new SourceLocation(
            start.getLine(),
            start.getCharPositionInLine(),
            snippet
        );
    }

    /**
     * Builds argument list from argListOpt context.
     */
    private List<ASTNode> buildArgList(AidemMediaParser.ArgListOptContext ctx) {
        if (ctx == null) {
            return List.of();
        }

        List<ASTNode> args = new ArrayList<>();
        for (AidemMediaParser.ArgContext argCtx : ctx.arg()) {
            ASTNode arg = visit(argCtx);
            if (arg != null) {
                args.add(arg);
            }
        }
        return args;
    }

    // ========================================
    // VISITOR METHODS
    // ========================================

    @Override
    public ASTNode visitScript(AidemMediaParser.ScriptContext ctx) {
        List<ASTNode> statements = new ArrayList<>();

        for (AidemMediaParser.StatementContext stmtCtx : ctx.statement()) {
            ASTNode stmt = visit(stmtCtx);
            if (stmt != null) {
                statements.add(stmt);
            }
        }

        return new BlockNode(statements, loc(ctx));
    }

    @Override
    public ASTNode visitStatement(AidemMediaParser.StatementContext ctx) {
        // Delegate to the appropriate sub-rule
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
    public ASTNode visitSpecialCall(AidemMediaParser.SpecialCallContext ctx) {
        String name = ctx.name.getText();
        List<ASTNode> args = buildArgList(ctx.argListOpt());
        SourceLocation location = loc(ctx);

        // Pattern matching would be nice here, but strings don't work with switch expressions yet
        // So we use enhanced switch (Java 14+)
        return switch (name) {
            case "BOOL", "STRING", "DOUBLE", "INT" -> buildVarDef(name, args, location);
            case "CONV" -> buildConv(args, location);
            case "RETURN" -> buildReturn(args, location);
            case "BREAK" -> new BreakNode(location);
            case "ONEBREAK" -> new OneBreakNode(location);
            case "GETAPPLICATIONNAME" -> buildGetApplicationName(location);
            case "GETCURRENTSCENE" -> buildGetCurrentScene(location);
            case "MSGBOX" -> null; // TODO: implement
            case "IF" -> buildIf(args, location);
            case "WHILE" -> buildWhile(args, location);
            case "LOOP", "FOR" -> buildLoop(name, args, location);
            default -> throw new ParseException(
                "Unknown special call: " + name,
                location
            );
        };
    }

    // === Variable Definition (@INT, @STRING, etc) ===

    private ASTNode buildVarDef(String type, List<ASTNode> args, SourceLocation location) {
        if (args.size() < 2) {
            throw new ParseException(
                "@" + type + " expects at least 2 arguments (name, value)",
                location
            );
        }

        // First arg is the variable name - must evaluate to string
        String varName = evaluateToString(args.get(0));
        ASTNode initialValue = args.get(1);

        return new VarDefNode(type, varName, initialValue, location);
    }

    /**
     * Evaluates a node to a string at parse time.
     * This is for cases where we need a constant string (like variable names).
     */
    private String evaluateToString(ASTNode node) {
        return switch (node) {
            case LiteralNode(StringValue v, var loc) -> v.value();
            case LiteralNode(IntValue v, var loc) -> String.valueOf(v.value());
            case LiteralNode(DoubleValue v, var loc) -> String.valueOf(v.value());
            case VariableNode(String name, var loc) -> name;
            default -> throw new ParseException(
                "Expected constant string, got: " + node,
                node.location()
            );
        };
    }

    // === Type Conversion (@CONV) ===

    private ASTNode buildConv(List<ASTNode> args, SourceLocation location) {
        if (args.size() < 2) {
            throw new ParseException(
                "@CONV expects 2 arguments (variable, targetType)",
                location
            );
        }

        ASTNode variable = args.get(0);
        String targetType = evaluateToString(args.get(1));

        return new ConvNode(variable, targetType, location);
    }

    // === Return (@RETURN) ===

    private ASTNode buildReturn(List<ASTNode> args, SourceLocation location) {
        if (args.isEmpty()) {
            return new ReturnNode(null, location);
        }
        return new ReturnNode(args.get(0), location);
    }

    // === System Functions ===

    private ASTNode buildGetApplicationName(SourceLocation location) {
        // For now, return a literal with the application name
        String appName = context != null && context.getGame() != null
            ? context.getGame().getApplicationVariable().getName()
            : "<unknown>";

        return new LiteralNode(new StringValue(appName), location);
    }

    private ASTNode buildGetCurrentScene(SourceLocation location) {
        String scene = context != null && context.getGame() != null
            ? context.getGame().getCurrentScene()
            : "<unknown>";

        return new LiteralNode(new StringValue(scene), location);
    }

    // === If Statement (@IF) ===

    private ASTNode buildIf(List<ASTNode> args, SourceLocation location) {
        if (args.size() != 3 && args.size() != 5) {
            throw new ParseException(
                "@IF expects 3 or 5 arguments, got " + args.size(),
                location
            );
        }

        ASTNode condition;
        ASTNode trueBranch;
        ASTNode falseBranch;

        if (args.size() == 3) {
            // @IF(condition, trueBranch, falseBranch)
            condition = parseConditionArg(args.get(0));
            trueBranch = parseCodeArg(args.get(1));
            falseBranch = parseCodeArg(args.get(2));
        } else {
            // @IF(left, operator, right, trueBranch, falseBranch)
            ASTNode left = args.get(0);
            String operator = evaluateToString(args.get(1));
            ASTNode right = args.get(2);

            condition = new ComparisonNode(
                left,
                right,
                ComparisonNode.ComparisonOp.fromString(operator),
                location
            );
            trueBranch = parseCodeArg(args.get(3));
            falseBranch = parseCodeArg(args.get(4));
        }

        return new IfNode(condition, trueBranch, falseBranch, location);
    }

    // === While Loop (@WHILE) ===

    private ASTNode buildWhile(List<ASTNode> args, SourceLocation location) {
        if (args.size() != 4) {
            throw new ParseException(
                "@WHILE expects 4 arguments (left, operator, right, body)",
                location
            );
        }

        ASTNode left = args.get(0);
        String operator = evaluateToString(args.get(1));
        ASTNode right = args.get(2);
        ASTNode body = parseCodeArg(args.get(3));

        ASTNode condition = new ComparisonNode(
            left,
            right,
            ComparisonNode.ComparisonOp.fromString(operator),
            location
        );

        return new WhileNode(condition, body, location);
    }

    // === Loop (@LOOP, @FOR) ===

    private ASTNode buildLoop(String name, List<ASTNode> args, SourceLocation location) {
        int expectedArgs = name.equals("LOOP") ? 4 : 5;

        if (args.size() < expectedArgs) {
            throw new ParseException(
                "@" + name + " expects at least " + expectedArgs + " arguments",
                location
            );
        }

        if (name.equals("LOOP")) {
            // @LOOP(body, start, diff, step)
            ASTNode body = parseCodeArg(args.get(0));
            ASTNode start = args.get(1);
            ASTNode diff = args.get(2);
            ASTNode step = args.get(3);

            return new LoopNode(start, diff, step, body, location);
        } else {
            // @FOR(iterator, body, start, diff, step)
            // TODO: Implement FOR with iterator variable
            throw new ParseException("@FOR not implemented yet", location);
        }
    }

    // ========================================
    // ARGUMENT PARSING HELPERS
    // ========================================

    /**
     * Parses a code argument (code block string) into an AST.
     * If the argument is a string literal containing code, parses it.
     * Otherwise returns the argument as-is.
     */
    private ASTNode parseCodeArg(ASTNode arg) {
        return switch (arg) {
            case LiteralNode lit when lit.value() instanceof StringValue(String value) ->
                parseCodeBlock(value, lit.location());
            default -> arg;
        };
    }

    /**
     * Parses a condition argument.
     * If it's a string, parses it as a condition expression.
     * Otherwise returns the argument as-is.
     */
    private ASTNode parseConditionArg(ASTNode arg) {
        return switch (arg) {
            case LiteralNode lit when lit.value() instanceof StringValue(String value) ->
                parseConditionString(value, lit.location());
            default -> arg;
        };
    }

    /**
     * Parses a code block string into an AST.
     */
    private ASTNode parseCodeBlock(String code, SourceLocation originalLocation) {
        String text = code.trim();

        // Add braces if missing
        if (!text.startsWith("{")) {
            text = "{" + text;
        }
        if (!text.endsWith("}")) {
            text = text + "}";
        }

        try {
            CharStream input = CharStreams.fromString(text);
            AidemMediaLexer lexer = new AidemMediaLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            AidemMediaParser parser = new AidemMediaParser(tokens);

            AidemMediaParser.ScriptContext scriptCtx = parser.script();

            ASTBuilder nested = new ASTBuilder(this.context);
            return nested.visit(scriptCtx);
        } catch (Exception e) {
            throw new ParseException(
                "Failed to parse code block: " + e.getMessage(),
                originalLocation
            );
        }
    }

    /**
     * Parses a condition string (like "x > 5 && y < 10") into an AST.
     * This is more complex - for now, just try to parse as expression.
     */
    private ASTNode parseConditionString(String condText, SourceLocation originalLocation) {
        try {
            CharStream input = CharStreams.fromString(condText);
            AidemMediaLexer lexer = new AidemMediaLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            AidemMediaParser parser = new AidemMediaParser(tokens);

            AidemMediaParser.ExprContext exprCtx = parser.expr();

            ASTBuilder nested = new ASTBuilder(this.context);
            return nested.visit(exprCtx);
        } catch (Exception e) {
            throw new ParseException(
                "Failed to parse condition: " + e.getMessage(),
                originalLocation
            );
        }
    }

    // ========================================
    // EXPRESSIONS
    // ========================================

    @Override
    public ASTNode visitExpr(AidemMediaParser.ExprContext ctx) {
        return visit(ctx.arithmeticExpr());
    }

    @Override
    public ASTNode visitArithmeticExpr(AidemMediaParser.ArithmeticExprContext ctx) {
        // Left-to-right evaluation: left op right op right op right ...
        // Yeah, it's not a bug...
        ASTNode result = visit(ctx.left);

        int childCount = ctx.getChildCount();
        for (int i = 1; i + 1 < childCount; i += 2) {
            String opText = ctx.getChild(i).getText();
            ASTNode right = visit(ctx.getChild(i + 1));

            ArithmeticNode.ArithmeticOp op = ArithmeticNode.ArithmeticOp.fromString(opText);
            result = new ArithmeticNode(result, right, op, loc(ctx));
        }

        return result;
    }

    @Override
    public ASTNode visitUnaryExpr(AidemMediaParser.UnaryExprContext ctx) {
        if (ctx.primary() != null) {
            return visit(ctx.primary());
        }

        // Unary operator: +, -, *
        String op = ctx.op.getText();
        ASTNode inner = visit(ctx.unaryExpr());

        return switch (op) {
            case "+" -> inner; // Unary plus does nothing
            case "-" -> new ArithmeticNode(
                new LiteralNode(new IntValue(0), loc(ctx)),
                inner,
                ArithmeticNode.ArithmeticOp.SUBTRACT,
                loc(ctx)
            ); // -x = 0 - x
            case "*" -> new PointerDerefNode(inner, loc(ctx)); // Dereference
            default -> throw new ParseException(
                "Unknown unary operator: " + op,
                loc(ctx)
            );
        };
    }

    @Override
    public ASTNode visitPrimary(AidemMediaParser.PrimaryContext ctx) {
        SourceLocation location = loc(ctx);

        // NUMBER
        TerminalNode num = ctx.NUMBER();
        if (num != null) {
            String text = num.getText();
            Value value = text.contains(".")
                ? new DoubleValue(Double.parseDouble(text))
                : new IntValue(Integer.parseInt(text));
            return new LiteralNode(value, location);
        }

        // TRUE / FALSE
        if (ctx.TRUE() != null) {
            return new LiteralNode(BoolValue.TRUE, location);
        }
        if (ctx.FALSE() != null) {
            return new LiteralNode(BoolValue.FALSE, location);
        }

        // STRING
        if (ctx.STRING() != null) {
            String text = ctx.STRING().getText();
            // Remove surrounding quotes
            if (text.length() >= 2 && text.startsWith("\"") && text.endsWith("\"")) {
                text = text.substring(1, text.length() - 1);
            }
            return new LiteralNode(new StringValue(text), location);
        }

        // CODE_BLOCK
        if (ctx.CODE_BLOCK() != null) {
            String raw = ctx.CODE_BLOCK().getText();
            // Remove surrounding quotes
            if (raw.length() >= 2 && raw.startsWith("\"") && raw.endsWith("\"")) {
                raw = raw.substring(1, raw.length() - 1);
            }
            // Return as string literal - will be parsed later if needed
            return new LiteralNode(new StringValue(raw), location);
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
            return visit(ctx.expr());
        }

        throw new ParseException(
            "Unsupported primary expression: " + ctx.getText(),
            location
        );
    }

    // ========================================
    // VARIABLES AND METHOD CALLS
    // ========================================

    @Override
    public ASTNode visitObjectName(AidemMediaParser.ObjectNameContext ctx) {
        String name = ctx.name.getText();
        SourceLocation location = loc(ctx);

        if (ctx.field != null) {
            // Struct access: TYPE|FIELD
            String field = ctx.field.getText();
            return new StructAccessNode(name, field, location);
        } else {
            // Simple variable reference
            return new VariableNode(name, location);
        }
    }

    @Override
    public ASTNode visitObjectReference(AidemMediaParser.ObjectReferenceContext ctx) {
        ASTNode inner = visit(ctx.primary());
        return new PointerDerefNode(inner, loc(ctx));
    }

    @Override
    public ASTNode visitMethodCall(AidemMediaParser.MethodCallContext ctx) {
        SourceLocation location = loc(ctx);

        // Target object
        ASTNode target;
        if (ctx.objectName() != null) {
            target = visit(ctx.objectName());
        } else if (ctx.objectReference() != null) {
            target = visit(ctx.objectReference());
        } else {
            throw new ParseException(
                "Method call without target",
                location
            );
        }

        // Method name
        String methodName = ctx.method.getText();

        // Arguments
        List<ASTNode> arguments = buildArgList(ctx.argListOpt());

        return new MethodCallNode(target, methodName, arguments, location);
    }

    @Override
    public ASTNode visitArg(AidemMediaParser.ArgContext ctx) {
        if (ctx.expr() != null) {
            return visit(ctx.expr());
        }

        if (ctx.missing_quote != null) {
            // Missing quote - add it back
            String text = ctx.missing_quote.getText() + "\"";
            return new LiteralNode(new StringValue(text), loc(ctx));
        }

        return null;
    }
}
