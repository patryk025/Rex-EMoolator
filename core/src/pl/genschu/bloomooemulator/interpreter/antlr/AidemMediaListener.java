// Generated from AidemMedia.g4 by ANTLR 4.13.2
package pl.genschu.bloomooemulator.interpreter.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link AidemMediaParser}.
 */
public interface AidemMediaListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(AidemMediaParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(AidemMediaParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(AidemMediaParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(AidemMediaParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(AidemMediaParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(AidemMediaParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(AidemMediaParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(AidemMediaParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#specialFunction}.
	 * @param ctx the parse tree
	 */
	void enterSpecialFunction(AidemMediaParser.SpecialFunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#specialFunction}.
	 * @param ctx the parse tree
	 */
	void exitSpecialFunction(AidemMediaParser.SpecialFunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#loopStatement}.
	 * @param ctx the parse tree
	 */
	void enterLoopStatement(AidemMediaParser.LoopStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#loopStatement}.
	 * @param ctx the parse tree
	 */
	void exitLoopStatement(AidemMediaParser.LoopStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(AidemMediaParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(AidemMediaParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#ifCondition}.
	 * @param ctx the parse tree
	 */
	void enterIfCondition(AidemMediaParser.IfConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#ifCondition}.
	 * @param ctx the parse tree
	 */
	void exitIfCondition(AidemMediaParser.IfConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#conditionSimple}.
	 * @param ctx the parse tree
	 */
	void enterConditionSimple(AidemMediaParser.ConditionSimpleContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#conditionSimple}.
	 * @param ctx the parse tree
	 */
	void exitConditionSimple(AidemMediaParser.ConditionSimpleContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#conditionComplex}.
	 * @param ctx the parse tree
	 */
	void enterConditionComplex(AidemMediaParser.ConditionComplexContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#conditionComplex}.
	 * @param ctx the parse tree
	 */
	void exitConditionComplex(AidemMediaParser.ConditionComplexContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#complexTerm}.
	 * @param ctx the parse tree
	 */
	void enterComplexTerm(AidemMediaParser.ComplexTermContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#complexTerm}.
	 * @param ctx the parse tree
	 */
	void exitComplexTerm(AidemMediaParser.ComplexTermContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#trueBranch}.
	 * @param ctx the parse tree
	 */
	void enterTrueBranch(AidemMediaParser.TrueBranchContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#trueBranch}.
	 * @param ctx the parse tree
	 */
	void exitTrueBranch(AidemMediaParser.TrueBranchContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#falseBranch}.
	 * @param ctx the parse tree
	 */
	void enterFalseBranch(AidemMediaParser.FalseBranchContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#falseBranch}.
	 * @param ctx the parse tree
	 */
	void exitFalseBranch(AidemMediaParser.FalseBranchContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#paramList}.
	 * @param ctx the parse tree
	 */
	void enterParamList(AidemMediaParser.ParamListContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#paramList}.
	 * @param ctx the parse tree
	 */
	void exitParamList(AidemMediaParser.ParamListContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(AidemMediaParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(AidemMediaParser.ParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(AidemMediaParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(AidemMediaParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#variableReference}.
	 * @param ctx the parse tree
	 */
	void enterVariableReference(AidemMediaParser.VariableReferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#variableReference}.
	 * @param ctx the parse tree
	 */
	void exitVariableReference(AidemMediaParser.VariableReferenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#structField}.
	 * @param ctx the parse tree
	 */
	void enterStructField(AidemMediaParser.StructFieldContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#structField}.
	 * @param ctx the parse tree
	 */
	void exitStructField(AidemMediaParser.StructFieldContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#mathExpression}.
	 * @param ctx the parse tree
	 */
	void enterMathExpression(AidemMediaParser.MathExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#mathExpression}.
	 * @param ctx the parse tree
	 */
	void exitMathExpression(AidemMediaParser.MathExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#mathOperator}.
	 * @param ctx the parse tree
	 */
	void enterMathOperator(AidemMediaParser.MathOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#mathOperator}.
	 * @param ctx the parse tree
	 */
	void exitMathOperator(AidemMediaParser.MathOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#mathFactor}.
	 * @param ctx the parse tree
	 */
	void enterMathFactor(AidemMediaParser.MathFactorContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#mathFactor}.
	 * @param ctx the parse tree
	 */
	void exitMathFactor(AidemMediaParser.MathFactorContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#primitive}.
	 * @param ctx the parse tree
	 */
	void enterPrimitive(AidemMediaParser.PrimitiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#primitive}.
	 * @param ctx the parse tree
	 */
	void exitPrimitive(AidemMediaParser.PrimitiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(AidemMediaParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(AidemMediaParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(AidemMediaParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(AidemMediaParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#functionName}.
	 * @param ctx the parse tree
	 */
	void enterFunctionName(AidemMediaParser.FunctionNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#functionName}.
	 * @param ctx the parse tree
	 */
	void exitFunctionName(AidemMediaParser.FunctionNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#structColumn}.
	 * @param ctx the parse tree
	 */
	void enterStructColumn(AidemMediaParser.StructColumnContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#structColumn}.
	 * @param ctx the parse tree
	 */
	void exitStructColumn(AidemMediaParser.StructColumnContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#inlineComment}.
	 * @param ctx the parse tree
	 */
	void enterInlineComment(AidemMediaParser.InlineCommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#inlineComment}.
	 * @param ctx the parse tree
	 */
	void exitInlineComment(AidemMediaParser.InlineCommentContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#comparator}.
	 * @param ctx the parse tree
	 */
	void enterComparator(AidemMediaParser.ComparatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#comparator}.
	 * @param ctx the parse tree
	 */
	void exitComparator(AidemMediaParser.ComparatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#logicOperator}.
	 * @param ctx the parse tree
	 */
	void enterLogicOperator(AidemMediaParser.LogicOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#logicOperator}.
	 * @param ctx the parse tree
	 */
	void exitLogicOperator(AidemMediaParser.LogicOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#string}.
	 * @param ctx the parse tree
	 */
	void enterString(AidemMediaParser.StringContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#string}.
	 * @param ctx the parse tree
	 */
	void exitString(AidemMediaParser.StringContext ctx);
}