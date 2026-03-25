package pl.genschu.bloomooemulator.interpreter.parser;

import pl.genschu.bloomooemulator.engine.config.EngineConfig;
import pl.genschu.bloomooemulator.interpreter.ast.*;
import pl.genschu.bloomooemulator.interpreter.errors.ParseException;
import pl.genschu.bloomooemulator.interpreter.errors.SourceLocation;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Custom recursive-descent parser for BlooMoo/Piklib script language.
 */
public class CodeParser {

    private static final int MAX_DEPTH = 10;

    /**
     * Tracks source text and lets the parser turn raw offsets back into line/column.
     */
    private record SourceText(String raw, int[] lineStarts, int baseLine, int baseColumn) {
        static SourceText from(String raw) {
            return from(raw, 1, 0);
        }

        static SourceText from(String raw, int baseLine, int baseColumn) {
            String safeRaw = raw != null ? raw : "";
            List<Integer> starts = new ArrayList<>();
            starts.add(0);
            for (int i = 0; i < safeRaw.length(); i++) {
                if (safeRaw.charAt(i) == '\n' && i + 1 <= safeRaw.length()) {
                    starts.add(i + 1);
                }
            }

            int[] lineStarts = new int[starts.size()];
            for (int i = 0; i < starts.size(); i++) {
                lineStarts[i] = starts.get(i);
            }

            return new SourceText(safeRaw, lineStarts, Math.max(baseLine, 1), Math.max(baseColumn, 0));
        }

        SourceLocation toLocation(SourceSpan span) {
            if (span == null) {
                return SourceLocation.UNKNOWN;
            }

            int offset = Math.max(0, Math.min(span.start(), raw.length()));
            int lineIndex = 0;
            for (int i = 0; i < lineStarts.length; i++) {
                if (lineStarts[i] > offset) {
                    break;
                }
                lineIndex = i;
            }

            int line = baseLine + lineIndex;
            int column = offset - lineStarts[lineIndex];
            if (lineIndex == 0) {
                column += baseColumn;
            }

            String snippet = span.text().trim();
            if (snippet.isEmpty()) {
                snippet = span.text();
            }

            return new SourceLocation(line, column, snippet);
        }
    }

    /**
     * A slice of the original source text. The parser operates on spans instead of plain strings
     * so it can keep source positions even when parsing nested inline procedures or conditions.
     */
    private record SourceSpan(SourceText source, int start, int end) {
        SourceSpan {
            if (source == null) {
                throw new IllegalArgumentException("source cannot be null");
            }
            start = Math.max(0, Math.min(start, source.raw().length()));
            end = Math.max(start, Math.min(end, source.raw().length()));
        }

        int length() {
            return end - start;
        }

        boolean isEmpty() {
            return length() == 0;
        }

        String text() {
            return source.raw().substring(start, end);
        }

        SourceSpan trim() {
            int newStart = start;
            int newEnd = end;

            while (newStart < newEnd && Character.isWhitespace(source.raw().charAt(newStart))) {
                newStart++;
            }
            while (newEnd > newStart && Character.isWhitespace(source.raw().charAt(newEnd - 1))) {
                newEnd--;
            }

            return new SourceSpan(source, newStart, newEnd);
        }

        String compactText() {
            String text = text();
            StringBuilder compact = new StringBuilder(text.length());
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c != ' ') {
                    compact.append(c);
                }
            }
            return compact.toString();
        }

        SourceSpan subspan(int relativeStart, int relativeEnd) {
            return new SourceSpan(source, start + relativeStart, start + relativeEnd);
        }

        SourceSpan between(int relativeStartInclusive, int relativeEndExclusive) {
            return new SourceSpan(source, start + relativeStartInclusive, start + relativeEndExclusive).trim();
        }
    }

    private record ParsedArgument(ASTNode node, SourceSpan source) {
    }

    private record ExpressionToken(String text, SourceSpan source, boolean operator) {
    }

    private record ComparisonSplit(SourceSpan left, String operator, SourceSpan right) {
    }

    /**
     * Parses a full script code block (typically wrapped in {...}) into an AST.
     *
     * @param code the raw script code, e.g. "{&#064;STRING("X","5");OBJ^METHOD();}"
     * @return BlockNode containing all parsed statements
     */
    public static ASTNode parseCode(String code) {
        SourceText source = SourceText.from(code);
        return parseCode(new SourceSpan(source, 0, source.raw().length()));
    }

    private static ASTNode parseCode(SourceSpan codeSpan) {
        SourceSpan prepared = prepareCode(codeSpan);
        List<SourceSpan> lines = splitLines(prepared);
        List<ASTNode> statements = new ArrayList<>();
        for (SourceSpan line : lines) {
            if (line.isEmpty()) continue;
            ASTNode node = parseLine(line, 0);
            if (node != null) {
                statements.add(node);
            }
        }
        return new BlockNode(statements, loc(codeSpan));
    }

    /**
     * Strips outer braces and whitespace while preserving source offsets.
     */
    private static SourceSpan prepareCode(SourceSpan code) {
        SourceSpan prepared = code.trim();
        String text = prepared.text();
        if (text.startsWith("{")) {
            prepared = prepared.subspan(1, prepared.length());
        }
        text = prepared.text();
        if (text.endsWith("}")) {
            prepared = prepared.subspan(0, prepared.length() - 1);
        }
        return prepared.trim();
    }

    /**
     * Splits code into individual statement lines at semicolons,
     * respecting parenthesis depth.
     */
    private static List<SourceSpan> splitLines(SourceSpan code) {
        List<SourceSpan> lines = new ArrayList<>();
        String text = code.text();
        int depth = 0;
        int statementStart = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '(') depth++;
            if (c == ')') depth--;

            if (c == ';' && depth == 0) {
                SourceSpan line = code.between(statementStart, i);
                if (!line.isEmpty()) {
                    lines.add(line);
                }
                statementStart = i + 1;
            }
        }

        if (EngineConfig.getInstance().isFixScriptsOnLoad()) {
            SourceSpan remaining = code.between(statementStart, text.length());
            if (!remaining.isEmpty()) {
                lines.add(remaining);
            }
        }

        return lines;
    }

    private static List<SourceSpan> splitArgs(SourceSpan argsText) {
        SourceSpan trimmed = argsText.trim();
        if (trimmed.isEmpty()) return List.of();

        List<SourceSpan> parts = new ArrayList<>();
        String text = trimmed.text();
        int start = 0;
        int depth = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '(') depth++;
            else if (c == ')') depth--;
            else if (c == ',' && depth == 0) {
                SourceSpan part = trimmed.between(start, i);
                if (!part.isEmpty()) {
                    parts.add(part);
                }
                start = i + 1;
            }
        }

        SourceSpan part = trimmed.between(start, text.length());
        if (!part.isEmpty()) {
            parts.add(part);
        }

        return parts;
    }

    private static ASTNode parseLine(SourceSpan line, int depth) {
        SourceSpan trimmed = line.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        if (depth > MAX_DEPTH) { // just a safeguard against infinite recursion in malformed code
            throw new ParseException("Max parsing depth exceeded", loc(trimmed));
        }

        String normalized = trimmed.compactText();
        if (normalized.isEmpty()) {
            return null;
        }

        char firstChar = normalized.charAt(0);

        if (firstChar == '@') {
            return parseAtCall(trimmed, depth);
        }

        if (firstChar == '*') {
            return parseStarTarget(trimmed, depth);
        }

        if (normalized.startsWith("THIS") && normalized.length() > 4) {
            return parseThisCall(trimmed, depth);
        }

        if (firstChar == '[') {
            return parseBracketExpression(trimmed);
        }

        return parseDefaultLine(trimmed, depth);
    }

    private static ASTNode parseAtCall(SourceSpan normalizedLine, int depth) {
        String text = normalizedLine.text();
        int parenIdx = text.indexOf('(');
        String name;
        SourceSpan argsSpan;

        if (parenIdx >= 0) {
            name = text.substring(1, parenIdx).trim();
            argsSpan = extractBetweenOuterParens(normalizedLine, parenIdx);
        } else {
            name = text.substring(1).trim();
            argsSpan = emptySpanAtEnd(normalizedLine);
        }

        return dispatchSpecialCall(name, argsSpan, depth, normalizedLine);
    }

    private static ASTNode parseStarTarget(SourceSpan normalizedLine, int depth) {
        String text = normalizedLine.text();
        int closeBracket = text.indexOf(']');
        int searchFrom = closeBracket >= 0 ? closeBracket + 1 : 1;
        int caretIdx = text.indexOf('^', searchFrom);

        int parenIdx;
        if (caretIdx >= 0) {
            parenIdx = text.indexOf('(', caretIdx);
        } else {
            parenIdx = text.indexOf('(', searchFrom);
        }

        int targetEnd = caretIdx >= 0 ? caretIdx : (parenIdx >= 0 ? parenIdx : text.length());
        SourceSpan targetSpan = normalizedLine.between(1, targetEnd);

        ASTNode derefTarget;
        if (!targetSpan.isEmpty() && targetSpan.text().trim().startsWith("[")) {
            derefTarget = parseExpression(stripOuterDelimiters(targetSpan, '[', ']'));
        } else {
            derefTarget = resolveTarget(targetSpan);
        }
        ASTNode target = new PointerDerefNode(derefTarget, loc(targetSpan));

        if (caretIdx >= 0) {
            int methodEnd = (parenIdx >= 0 ? parenIdx : text.length());
            String methodName = text.substring(caretIdx + 1, methodEnd).trim();
            SourceSpan argsSpan = parenIdx >= 0
                ? extractBetweenOuterParens(normalizedLine, parenIdx)
                : emptySpanAtEnd(normalizedLine);
            List<ParsedArgument> args = splitAndParseArgs(argsSpan, depth);
            return new MethodCallNode(target, methodName, unpack(args), loc(normalizedLine));
        }

        return target;
    }

    private static ASTNode parseThisCall(SourceSpan normalizedLine, int depth) {
        String text = normalizedLine.text();
        int thisIdx = text.indexOf("THIS");
        int start = thisIdx >= 0 ? thisIdx + 4 : 4;
        if (start < text.length() && text.charAt(start) == '^') {
            start++;
        }

        int parenIdx = text.indexOf('(', start);
        int methodEnd = parenIdx >= 0 ? parenIdx : text.length();
        String methodName = text.substring(start, methodEnd).trim();
        SourceSpan argsText = parenIdx >= 0
            ? extractBetweenOuterParens(normalizedLine, parenIdx)
            : emptySpanAtEnd(normalizedLine);

        SourceSpan thisSpan = normalizedLine.between(Math.max(thisIdx, 0), Math.min((Math.max(thisIdx, 0)) + 4, normalizedLine.length()));
        ASTNode target = new VariableNode("THIS", loc(thisSpan));
        List<ParsedArgument> args = splitAndParseArgs(argsText, depth);

        if (methodName.isEmpty()) {
            return target;
        }
        return new MethodCallNode(target, methodName, unpack(args), loc(normalizedLine));
    }

    private static ASTNode parseBracketExpression(SourceSpan normalizedLine) {
        return parseExpression(stripOuterDelimiters(normalizedLine, '[', ']'));
    }

    private static ASTNode parseDefaultLine(SourceSpan normalizedLine, int depth) {
        String text = normalizedLine.text();
        int caretIdx = text.indexOf('^');
        String compact = normalizedLine.compactText();
        char firstChar = compact.charAt(0);

        if (firstChar == '"' || caretIdx < 0) {
            return parseOperand(normalizedLine);
        }

        int parenIdx = text.indexOf('(', caretIdx);
        int methodEnd = parenIdx >= 0 ? parenIdx : text.length();
        String methodName = text.substring(caretIdx + 1, methodEnd).trim();
        SourceSpan targetSpan = normalizedLine.between(0, caretIdx);
        SourceSpan argsSpan = parenIdx >= 0
            ? extractBetweenOuterParens(normalizedLine, parenIdx)
            : emptySpanAtEnd(normalizedLine);

        ASTNode target = resolveTarget(targetSpan);
        List<ParsedArgument> args = splitAndParseArgs(argsSpan, depth);
        return new MethodCallNode(target, methodName, unpack(args), loc(normalizedLine));
    }

    // ========================================
    // SPECIAL CALL DISPATCH
    // ========================================

    private static ASTNode dispatchSpecialCall(String name, SourceSpan argsText, int depth, SourceSpan callSpan) {
        List<ParsedArgument> args = splitAndParseArgs(argsText, depth);
        SourceLocation location = loc(callSpan);

        return switch (name) {
            case "INT", "STRING", "DOUBLE", "BOOL" -> {
                if (args.size() < 2) {
                    throw new ParseException("@" + name + " expects at least 2 arguments", location);
                }
                String varName = extractStringValue(args.get(0).node());
                ASTNode value = args.get(1).node();
                yield new VarDefNode(name, varName, value, location);
            }
            case "IF" -> buildIf(args, location);
            case "WHILE" -> buildWhile(args, location);
            case "LOOP" -> buildLoop(args, location);
            case "FOR" -> throw new ParseException("@FOR not implemented yet", location);
            case "RETURN" -> new ReturnNode(args.isEmpty() ? null : args.get(0).node(), location);
            case "BREAK" -> new BreakNode(location);
            case "ONEBREAK" -> new OneBreakNode(location);
            case "CONV" -> {
                if (args.size() < 2) {
                    throw new ParseException("@CONV expects 2 arguments", location);
                }
                String targetType = extractStringValue(args.get(1).node());
                yield new ConvNode(args.get(0).node(), targetType, location);
            }
            default -> throw new ParseException("Unknown special call: @" + name, location);
        };
    }

    private static ASTNode buildIf(List<ParsedArgument> args, SourceLocation location) {
        if (args.size() == 3) {
            ASTNode condition = reinterpretAsCondition(args.get(0));
            ASTNode trueBranch = reinterpretAsCodeBlock(args.get(1));
            ASTNode falseBranch = reinterpretAsCodeBlock(args.get(2));
            return new IfNode(condition, trueBranch, falseBranch, location);
        }
        if (args.size() == 5) {
            String left = extractStringValue(args.get(0).node());
            String operator = extractStringValue(args.get(1).node());
            String right = extractStringValue(args.get(2).node());
            String condText = left + operator.replace("_", "'") + right;

            SourceSpan combinedSpan = merge(args.get(0).source(), args.get(2).source());
            ASTNode condition = parseCondition(derivedSpan(condText, combinedSpan));
            ASTNode trueBranch = reinterpretAsCodeBlock(args.get(3));
            ASTNode falseBranch = reinterpretAsCodeBlock(args.get(4));
            return new IfNode(condition, trueBranch, falseBranch, location);
        }
        throw new ParseException("@IF expects 3 or 5 arguments, got " + args.size(), location);
    }

    private static ASTNode buildWhile(List<ParsedArgument> args, SourceLocation location) {
        if (args.size() != 4) {
            throw new ParseException("@WHILE expects 4 arguments", location);
        }
        ASTNode left = args.get(0).node();
        String op = extractStringValue(args.get(1).node());
        ASTNode right = args.get(2).node();
        ASTNode body = reinterpretAsCodeBlock(args.get(3));
        ASTNode condition = new ComparisonNode(
            left,
            right,
            ComparisonNode.ComparisonOp.fromString(op),
            loc(merge(args.get(0).source(), args.get(2).source()))
        );
        return new WhileNode(condition, body, location);
    }

    private static ASTNode buildLoop(List<ParsedArgument> args, SourceLocation location) {
        if (args.size() < 4) {
            throw new ParseException("@LOOP expects 4 arguments", location);
        }
        ASTNode body = reinterpretAsCodeBlock(args.get(0));
        ASTNode start = args.get(1).node();
        ASTNode diff = args.get(2).node();
        ASTNode step = args.get(3).node();
        return new LoopNode(start, diff, step, body, location);
    }

    // ========================================
    // ARGUMENT PARSING
    // ========================================

    private static List<ParsedArgument> splitAndParseArgs(SourceSpan argsText, int depth) {
        List<ParsedArgument> result = new ArrayList<>();
        for (SourceSpan token : splitArgs(argsText)) {
            SourceSpan trimmed = token.trim();
            result.add(new ParsedArgument(parseArgument(trimmed, depth), trimmed));
        }
        return result;
    }

    private static ASTNode parseArgument(SourceSpan token, int depth) {
        if (token.isEmpty()) {
            return new LiteralNode(new StringValue(""), loc(token));
        }

        String normalized = token.compactText();
        char firstChar = normalized.charAt(0);
        boolean hasDollar = normalized.contains("$");

        if (firstChar == '[' && !hasDollar) {
            return parseExpression(stripOuterDelimiters(token, '[', ']'));
        }

        if (firstChar != '@' && !hasDollar && !normalized.contains("^")) {
            if (firstChar == '&') {
                return new VariableNode(normalized.substring(1), loc(token));
            }
            return parseLine(token, depth + 1);
        }

        int quoteOffset = firstChar == '"' ? 1 : 0;
        if (normalized.length() > quoteOffset && normalized.charAt(quoteOffset) == '{') {
            SourceSpan codeSpan = firstChar == '"' ? stripOuterDelimiters(token, '"', '"') : token;
            return parseCode(codeSpan);
        }

        return parseLine(token, depth + 1);
    }

    // ========================================
    // EXPRESSION PARSING (arithmetic, left-to-right, no precedence)
    // ========================================

    private static ASTNode parseExpression(SourceSpan exprSpan) {
        SourceSpan trimmed = exprSpan.trim();
        List<ExpressionToken> tokens = tokenizeExpression(trimmed);
        if (tokens == null || tokens.isEmpty()) {
            return parseOperand(trimmed);
        }

        ASTNode result = parseOperand(tokens.get(0).source());
        for (int i = 1; i + 1 < tokens.size(); i += 2) {
            String op = tokens.get(i).text();
            ASTNode right = parseOperand(tokens.get(i + 1).source());
            ArithmeticNode.ArithmeticOp arithmeticOp = ArithmeticNode.ArithmeticOp.fromString(op);
            result = new ArithmeticNode(result, right, arithmeticOp, loc(trimmed));
        }
        return result;
    }

    private static List<ExpressionToken> tokenizeExpression(SourceSpan exprSpan) {
        String text = exprSpan.text();
        List<ExpressionToken> tokens = new ArrayList<>();
        int currentStart = -1;
        boolean expectValue = true;
        int parenDepth = 0;
        int bracketDepth = 0;
        boolean inString = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '"') {
                if (currentStart < 0) {
                    currentStart = i;
                }
                inString = !inString;
                expectValue = false;
                continue;
            }
            if (inString) {
                if (currentStart < 0) {
                    currentStart = i;
                }
                expectValue = false;
                continue;
            }
            if (c == '[') {
                if (currentStart < 0) {
                    currentStart = i;
                }
                bracketDepth++;
                expectValue = false;
                continue;
            }
            if (c == ']') {
                bracketDepth--;
                expectValue = false;
                continue;
            }
            if (c == '(') {
                if (currentStart < 0) {
                    currentStart = i;
                }
                parenDepth++;
                expectValue = false;
                continue;
            }
            if (c == ')') {
                parenDepth--;
                expectValue = false;
                continue;
            }
            if (c == ' ') continue;
            if ("+-*%@".indexOf(c) >= 0 && parenDepth == 0 && bracketDepth == 0) {
                if (c == '-' && expectValue) {
                    if (currentStart < 0) {
                        currentStart = i;
                    }
                    continue;
                }

                if (currentStart < 0) {
                    return null;
                }

                SourceSpan operandSpan = exprSpan.between(currentStart, i);
                if (operandSpan.isEmpty()) {
                    return null;
                }
                tokens.add(new ExpressionToken(operandSpan.text(), operandSpan, false));
                tokens.add(new ExpressionToken(String.valueOf(c), exprSpan.subspan(i, i + 1), true));
                currentStart = -1;
                expectValue = true;
                continue;
            }

            if (currentStart < 0) {
                currentStart = i;
            }
            expectValue = false;
        }

        if (currentStart < 0) {
            return null;
        }

        SourceSpan operandSpan = exprSpan.between(currentStart, text.length());
        if (operandSpan.isEmpty()) {
            return null;
        }
        tokens.add(new ExpressionToken(operandSpan.text(), operandSpan, false));

        if (tokens.size() % 2 == 0) return null;
        if (tokens.size() == 1) return null;
        return tokens;
    }

    // ========================================
    // CONDITION PARSING (for @IF conditions)
    // ========================================

    static ASTNode parseCondition(String condText) {
        SourceText source = SourceText.from(condText);
        return parseCondition(new SourceSpan(source, 0, source.raw().length()));
    }

    private static ASTNode parseCondition(SourceSpan condSpan) {
        SourceSpan trimmed = condSpan.trim();
        String condText = trimmed.text();

        boolean hasAnd = condText.contains("&&");
        boolean hasOr = condText.contains("||");

        if (hasAnd || hasOr) {
            String delimiter = hasOr ? "||" : "&&";
            LogicalNode.LogicalOp op = hasOr ? LogicalNode.LogicalOp.OR : LogicalNode.LogicalOp.AND;

            List<SourceSpan> parts = splitByDelimiter(trimmed, delimiter);
            ASTNode result = parseCondition(parts.get(0));
            for (int i = 1; i < parts.size(); i++) {
                ASTNode right = parseCondition(parts.get(i));
                result = new LogicalNode(result, right, op, loc(trimmed));
            }
            return result;
        }

        return parseSingleCondition(trimmed);
    }

    private static ASTNode parseSingleCondition(SourceSpan expr) {
        ComparisonSplit split = splitComparison(expr.trim());
        if (split != null) {
            ASTNode left = parseConditionOperand(split.left());
            ASTNode right = parseConditionOperand(split.right());
            ComparisonNode.ComparisonOp op = ComparisonNode.ComparisonOp.fromString(split.operator());
            return new ComparisonNode(left, right, op, loc(expr));
        }
        return parseConditionOperand(expr);
    }

    private static ComparisonSplit splitComparison(SourceSpan expr) {
        String text = expr.text();

        int apos = text.indexOf("'");
        if (apos >= 0) {
            String op = "'";
            int leftEnd = apos;
            if (apos > 0) {
                char before = text.charAt(apos - 1);
                if (before == '!' || before == '<' || before == '>') {
                    op = before + "'";
                    leftEnd--;
                }
            }
            return new ComparisonSplit(
                expr.between(0, leftEnd),
                op,
                expr.between(apos + 1, text.length())
            );
        }

        for (String op : new String[]{"<", ">", "?"}) {
            int pos = text.indexOf(op);
            if (pos >= 0) {
                return new ComparisonSplit(
                    expr.between(0, pos),
                    op,
                    expr.between(pos + op.length(), text.length())
                );
            }
        }

        return null;
    }

    // ========================================
    // OPERAND PARSING
    // ========================================

    private static ASTNode parseOperand(SourceSpan tokenSpan) {
        SourceSpan token = tokenSpan.trim();
        if (token.isEmpty()) {
            return new LiteralNode(new StringValue(""), loc(token));
        }

        String text = token.text();
        if (text.startsWith("[") && text.endsWith("]")) {
            return parseExpression(stripOuterDelimiters(token, '[', ']'));
        }

        if (text.length() >= 2 && text.charAt(0) == '"' && text.charAt(text.length() - 1) == '"') {
            return new LiteralNode(new StringValue(text.substring(1, text.length() - 1)), loc(token));
        }

        if (text.equalsIgnoreCase("TRUE")) {
            return new LiteralNode(BoolValue.TRUE, loc(token));
        }
        if (text.equalsIgnoreCase("FALSE")) {
            return new LiteralNode(BoolValue.FALSE, loc(token));
        }

        try {
            if (text.contains(".")) {
                return new LiteralNode(new DoubleValue(Double.parseDouble(text)), loc(token));
            }
            return new LiteralNode(new IntValue(Integer.parseInt(text)), loc(token));
        } catch (NumberFormatException ignored) {
            // Not a number.
        }

        if (text.contains("^")) {
            return parseLine(token, 0);
        }
        if (text.contains("|")) {
            return resolveTarget(token);
        }

        return new VariableNode(text, loc(token));
    }

    private static ASTNode parseConditionOperand(SourceSpan tokenSpan) {
        SourceSpan token = tokenSpan.trim();
        if (token.isEmpty()) {
            return new LiteralNode(new StringValue(""), loc(token));
        }

        String text = token.text();
        if (text.startsWith("[") && text.endsWith("]")) {
            return parseExpression(stripOuterDelimiters(token, '[', ']'));
        }
        if (text.contains("^")) {
            return parseLine(token, 0);
        }
        if (text.contains("|")) {
            return resolveTarget(token);
        }
        if (containsArithmeticOperator(text)) {
            return new LiteralNode(new StringValue(text), loc(token));
        }
        return parseOperand(token);
    }

    // ========================================
    // TARGET RESOLUTION
    // ========================================

    private static ASTNode resolveTarget(SourceSpan targetSpan) {
        SourceSpan trimmed = targetSpan.trim();
        String target = trimmed.text();
        if (target.contains("$")) {
            return new LiteralNode(new StringValue(target), loc(trimmed));
        }
        if (target.contains("|")) {
            String[] parts = target.split("\\|", 2);
            return new StructAccessNode(parts[0].trim(), parts[1].trim(), loc(trimmed));
        }
        return new VariableNode(target, loc(trimmed));
    }

    // ========================================
    // RE-INTERPRETATION HELPERS
    // ========================================

    private static ASTNode reinterpretAsCondition(ParsedArgument arg) {
        if (arg.node() instanceof LiteralNode lit && lit.value() instanceof StringValue) {
            return parseCondition(literalContent(arg.source()));
        }
        return arg.node();
    }

    private static ASTNode reinterpretAsCodeBlock(ParsedArgument arg) {
        if (arg.node() instanceof LiteralNode lit && lit.value() instanceof StringValue) {
            SourceSpan code = literalContent(arg.source());
            if (!code.trim().isEmpty()) {
                return parseCode(code);
            }
        }
        return arg.node();
    }

    // ========================================
    // UTILITY METHODS
    // ========================================

    private static List<ASTNode> unpack(List<ParsedArgument> args) {
        List<ASTNode> nodes = new ArrayList<>(args.size());
        for (ParsedArgument arg : args) {
            nodes.add(arg.node());
        }
        return nodes;
    }

    private static String extractStringValue(ASTNode node) {
        return switch (node) {
            case LiteralNode(StringValue v, var loc) -> v.value();
            case LiteralNode(IntValue v, var loc) -> String.valueOf(v.value());
            case LiteralNode(DoubleValue v, var loc) -> String.valueOf(v.value());
            case LiteralNode(BoolValue v, var loc) -> String.valueOf(v.value());
            case VariableNode(String name, var loc) -> name;
            default -> throw new ParseException(
                "Expected constant string, got: " + node,
                node.location()
            );
        };
    }

    private static SourceSpan extractBetweenOuterParens(SourceSpan text, int openParen) {
        int closeParen = text.text().lastIndexOf(')');
        if (closeParen <= openParen) {
            return emptySpanAtEnd(text);
        }
        return text.between(openParen + 1, closeParen);
    }

    private static SourceSpan emptySpanAtEnd(SourceSpan span) {
        return span.subspan(span.length(), span.length());
    }

    private static SourceSpan stripOuterDelimiters(SourceSpan span, char open, char close) {
        SourceSpan trimmed = span.trim();
        String text = trimmed.text();
        if (text.length() >= 2 && text.charAt(0) == open && text.charAt(text.length() - 1) == close) {
            return trimmed.subspan(1, trimmed.length() - 1).trim();
        }
        return trimmed;
    }

    private static SourceSpan literalContent(SourceSpan literalSpan) {
        return stripOuterDelimiters(literalSpan, '"', '"');
    }

    private static List<SourceSpan> splitByDelimiter(SourceSpan span, String delimiter) {
        List<SourceSpan> parts = new ArrayList<>();
        String text = span.text();
        int start = 0;

        while (start <= text.length()) {
            int idx = text.indexOf(delimiter, start);
            if (idx < 0) {
                SourceSpan tail = span.between(start, text.length());
                if (!tail.isEmpty()) {
                    parts.add(tail);
                }
                break;
            }

            SourceSpan part = span.between(start, idx);
            if (!part.isEmpty()) {
                parts.add(part);
            }
            start = idx + delimiter.length();
        }

        return parts;
    }

    private static boolean containsArithmeticOperator(String text) {
        boolean inString = false;
        int parenDepth = 0;
        int bracketDepth = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '"') {
                inString = !inString;
                continue;
            }
            if (inString) {
                continue;
            }
            if (c == '[') {
                bracketDepth++;
                continue;
            }
            if (c == ']') {
                bracketDepth--;
                continue;
            }
            if (c == '(') {
                parenDepth++;
                continue;
            }
            if (c == ')') {
                parenDepth--;
                continue;
            }
            if (parenDepth == 0 && bracketDepth == 0 && "+-*%@".indexOf(c) >= 0) {
                return true;
            }
        }

        return false;
    }

    private static SourceSpan merge(SourceSpan first, SourceSpan second) {
        if (first == null) return second;
        if (second == null) return first;
        if (first.source() != second.source()) return first;
        return new SourceSpan(
            first.source(),
            Math.min(first.start(), second.start()),
            Math.max(first.end(), second.end())
        );
    }

    private static SourceSpan derivedSpan(String text, SourceSpan anchor) {
        SourceLocation location = loc(anchor);
        SourceText source = SourceText.from(text, location.line(), location.column());
        return new SourceSpan(source, 0, source.raw().length());
    }

    private static SourceLocation loc(SourceSpan span) {
        if (span == null) {
            return SourceLocation.UNKNOWN;
        }

        SourceSpan trimmed = span.trim();
        if (trimmed.isEmpty()) {
            return span.source().toLocation(span);
        }
        return trimmed.source().toLocation(trimmed);
    }
}
