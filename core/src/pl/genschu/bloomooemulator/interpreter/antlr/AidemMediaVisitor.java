// Generated from AidemMedia.g4 by ANTLR 4.13.2
package pl.genschu.bloomooemulator.interpreter.antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link AidemMediaParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface AidemMediaVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(AidemMediaParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(AidemMediaParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(AidemMediaParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#functionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(AidemMediaParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#specialFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpecialFunction(AidemMediaParser.SpecialFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#loopStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoopStatement(AidemMediaParser.LoopStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(AidemMediaParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#ifCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfCondition(AidemMediaParser.IfConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#conditionSimple}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionSimple(AidemMediaParser.ConditionSimpleContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#conditionComplex}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionComplex(AidemMediaParser.ConditionComplexContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#complexTerm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComplexTerm(AidemMediaParser.ComplexTermContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#trueBranch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrueBranch(AidemMediaParser.TrueBranchContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#falseBranch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFalseBranch(AidemMediaParser.FalseBranchContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#paramList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParamList(AidemMediaParser.ParamListContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(AidemMediaParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(AidemMediaParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#variableReference}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableReference(AidemMediaParser.VariableReferenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#structField}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStructField(AidemMediaParser.StructFieldContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#mathExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathExpression(AidemMediaParser.MathExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#mathOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathOperator(AidemMediaParser.MathOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#mathFactor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMathFactor(AidemMediaParser.MathFactorContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#primitive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimitive(AidemMediaParser.PrimitiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(AidemMediaParser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#variable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(AidemMediaParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#functionName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionName(AidemMediaParser.FunctionNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#structColumn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStructColumn(AidemMediaParser.StructColumnContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#inlineComment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInlineComment(AidemMediaParser.InlineCommentContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#comparator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparator(AidemMediaParser.ComparatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#logicOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicOperator(AidemMediaParser.LogicOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link AidemMediaParser#string}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString(AidemMediaParser.StringContext ctx);
}