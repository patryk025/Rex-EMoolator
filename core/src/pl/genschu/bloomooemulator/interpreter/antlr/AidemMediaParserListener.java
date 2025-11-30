// Generated from AidemMediaParser.g4 by ANTLR 4.13.2
package pl.genschu.bloomooemulator.interpreter.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link AidemMediaParser}.
 */
public interface AidemMediaParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#script}.
	 * @param ctx the parse tree
	 */
	void enterScript(AidemMediaParser.ScriptContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#script}.
	 * @param ctx the parse tree
	 */
	void exitScript(AidemMediaParser.ScriptContext ctx);
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
	 * Enter a parse tree produced by {@link AidemMediaParser#specialCall}.
	 * @param ctx the parse tree
	 */
	void enterSpecialCall(AidemMediaParser.SpecialCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#specialCall}.
	 * @param ctx the parse tree
	 */
	void exitSpecialCall(AidemMediaParser.SpecialCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#methodCall}.
	 * @param ctx the parse tree
	 */
	void enterMethodCall(AidemMediaParser.MethodCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#methodCall}.
	 * @param ctx the parse tree
	 */
	void exitMethodCall(AidemMediaParser.MethodCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#objectName}.
	 * @param ctx the parse tree
	 */
	void enterObjectName(AidemMediaParser.ObjectNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#objectName}.
	 * @param ctx the parse tree
	 */
	void exitObjectName(AidemMediaParser.ObjectNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#objectReference}.
	 * @param ctx the parse tree
	 */
	void enterObjectReference(AidemMediaParser.ObjectReferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#objectReference}.
	 * @param ctx the parse tree
	 */
	void exitObjectReference(AidemMediaParser.ObjectReferenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#argListOpt}.
	 * @param ctx the parse tree
	 */
	void enterArgListOpt(AidemMediaParser.ArgListOptContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#argListOpt}.
	 * @param ctx the parse tree
	 */
	void exitArgListOpt(AidemMediaParser.ArgListOptContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#arg}.
	 * @param ctx the parse tree
	 */
	void enterArg(AidemMediaParser.ArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#arg}.
	 * @param ctx the parse tree
	 */
	void exitArg(AidemMediaParser.ArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(AidemMediaParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(AidemMediaParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#addExpr}.
	 * @param ctx the parse tree
	 */
	void enterAddExpr(AidemMediaParser.AddExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#addExpr}.
	 * @param ctx the parse tree
	 */
	void exitAddExpr(AidemMediaParser.AddExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#mulExpr}.
	 * @param ctx the parse tree
	 */
	void enterMulExpr(AidemMediaParser.MulExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#mulExpr}.
	 * @param ctx the parse tree
	 */
	void exitMulExpr(AidemMediaParser.MulExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#unaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpr(AidemMediaParser.UnaryExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#unaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpr(AidemMediaParser.UnaryExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link AidemMediaParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary(AidemMediaParser.PrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link AidemMediaParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary(AidemMediaParser.PrimaryContext ctx);
}